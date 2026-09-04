#!/usr/bin/env python3
"""Drive a GS1-heavy self-play game over the gym-server HTTP API."""

from __future__ import annotations

import json
import sys
import time
import urllib.error
import urllib.request

BASE = "http://localhost:8081"
MAX_STEPS = 2000

DECK = {
    "Forest": 10,
    "Island": 8,
    "Mountain": 6,
    "Jiang Yanggu": 2,
    "Mu Yanling": 2,
    "Journey for the Elixir": 2,
    "Stormcloud Spirit": 4,
    "Purple-Crystal Crab": 4,
    "Fire-Omen Crane": 2,
    "Rhythmic Water Vortex": 2,
}


def post(path: str, body: dict | None = None) -> dict:
    data = None if body is None else json.dumps(body).encode()
    req = urllib.request.Request(
        f"{BASE}{path}",
        data=data,
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    with urllib.request.urlopen(req, timeout=120) as resp:
        return json.loads(resp.read())


def get(path: str) -> dict:
    with urllib.request.urlopen(f"{BASE}{path}", timeout=30) as resp:
        return json.loads(resp.read())


def zone_cards(obs: dict, zone_type: str, owner_id: str | None = None) -> list[dict]:
    cards: list[dict] = []
    for zone in obs.get("zones") or []:
        if zone.get("zoneType") != zone_type:
            continue
        if owner_id is not None and zone.get("ownerId") != owner_id:
            continue
        cards.extend(zone.get("cards") or [])
    return cards


def battlefield_cards(obs: dict, owner_id: str | None = None) -> list[dict]:
    return zone_cards(obs, "BATTLEFIELD", owner_id)


def library_cards(obs: dict, owner_id: str | None = None) -> list[dict]:
    return zone_cards(obs, "LIBRARY", owner_id)


def graveyard_cards(obs: dict, owner_id: str | None = None) -> list[dict]:
    return zone_cards(obs, "GRAVEYARD", owner_id)


def hand_cards(obs: dict, owner_id: str | None = None) -> list[dict]:
    return zone_cards(obs, "HAND", owner_id)


def _matches_library_filter(card: dict, prompt: str) -> bool:
    """Best-effort filter match using the decision prompt (HTTP API hides legal options)."""
    text = prompt.lower()
    types = {t.upper() for t in (card.get("types") or [])}
    subtypes = {s.upper() for s in (card.get("subtypes") or [])}
    name = (card.get("name") or "").lower()

    if "basic land" in text:
        return "LAND" in types and "BASIC" in subtypes

    marker = "named "
    if marker in text:
        wanted = text.split(marker, 1)[1].split(",", 1)[0].strip().strip(".")
        return name == wanted or wanted in name

    if "land" in text and "creature" not in text:
        return "LAND" in types

    return True


def pick_cards_for_selection(
    obs: dict,
    pending: dict,
    *,
    max_pick: int,
    min_pick: int,
) -> list[str]:
    player_id = pending.get("playerId")
    prompt = pending.get("prompt") or pending.get("effectHint") or ""
    kind = pending.get("kind", "")

    if kind == "SEARCH_LIBRARY":
        pool = library_cards(obs, player_id) + graveyard_cards(obs, player_id)
        pool = [c for c in pool if _matches_library_filter(c, prompt)]
    elif kind == "SELECT_CARDS":
        pool = (
            hand_cards(obs, player_id)
            + graveyard_cards(obs, player_id)
            + library_cards(obs, player_id)
            + battlefield_cards(obs, player_id)
        )
    else:
        pool = library_cards(obs, player_id) or battlefield_cards(obs, player_id)

    entity_ids = [c["entityId"] for c in pool if c.get("entityId")]
    if not entity_ids:
        return [] if min_pick == 0 else []

    take = min(max_pick, len(entity_ids)) if max_pick else len(entity_ids)
    if take < min_pick:
        take = min(min_pick, len(entity_ids))
    return entity_ids[:take]


def build_structured_response(obs: dict, pending: dict) -> dict | None:
    """Map pendingDecision.kind to the DecisionResponse shape the gym API expects."""
    decision_id = pending["decisionId"]
    kind = pending.get("kind", "")
    shape = pending.get("shape") or {}
    player_id = pending.get("playerId")

    if kind == "YES_NO":
        return {"type": "YesNoResponse", "decisionId": decision_id, "choice": True}

    if kind == "SELECT_MANA_SOURCES":
        return {
            "type": "ManaSourcesSelectedResponse",
            "decisionId": decision_id,
            "autoPay": True,
        }

    if kind == "CHOOSE_NUMBER":
        number = shape.get("numericMin")
        if number is None:
            number = shape.get("numericMax", 0)
        return {"type": "NumberChosenResponse", "decisionId": decision_id, "number": number}

    if kind == "CHOOSE_COLOR":
        colors = shape.get("availableColors") or []
        if not colors:
            return None
        return {"type": "ColorChosenResponse", "decisionId": decision_id, "color": colors[0]}

    if kind == "CHOOSE_MODE":
        return {"type": "ModesChosenResponse", "decisionId": decision_id, "selectedModes": [0]}

    if kind == "CHOOSE_OPTION":
        return {"type": "OptionChosenResponse", "decisionId": decision_id, "optionIndex": 0}

    if kind in ("SELECT_CARDS", "SEARCH_LIBRARY"):
        max_pick = shape.get("maxSelections") or 1
        min_pick = shape.get("minSelections") or 0
        selected = pick_cards_for_selection(
            obs, pending, max_pick=max_pick, min_pick=min_pick
        )
        if len(selected) < min_pick and min_pick > 0:
            return None
        return {
            "type": "CardsSelectedResponse",
            "decisionId": decision_id,
            "selectedCards": selected,
        }

    if kind == "CHOOSE_TARGETS":
        opponents = [
            p["id"]
            for p in obs.get("players") or []
            if p.get("id") != player_id
        ]
        target = None
        for opp_id in opponents:
            for card in battlefield_cards(obs, opp_id):
                if "CREATURE" in (card.get("types") or []):
                    target = card["entityId"]
                    break
            if target:
                break
        if target is None:
            own = battlefield_cards(obs, player_id)
            if own:
                target = own[0]["entityId"]
        if target is None:
            return None
        return {
            "type": "TargetsResponse",
            "decisionId": decision_id,
            "selectedTargets": {"0": [target]},
        }

    if kind in ("ORDER_OBJECTS", "REORDER_LIBRARY"):
        pool = library_cards(obs, player_id)
        ordered = [c["entityId"] for c in pool if c.get("entityId")]
        if not ordered:
            return None
        return {"type": "OrderedResponse", "decisionId": decision_id, "orderedObjects": ordered}

    if kind == "ASSIGN_DAMAGE":
        return {"type": "DamageAssignmentResponse", "decisionId": decision_id, "assignments": {}}

    if kind == "DISTRIBUTE":
        total = shape.get("totalToDistribute") or 0
        target = None
        for card in battlefield_cards(obs):
            if "CREATURE" in (card.get("types") or []):
                target = card["entityId"]
                break
        if target is None:
            return None
        return {
            "type": "DistributionResponse",
            "decisionId": decision_id,
            "distribution": {target: total},
        }

    return None


def build_step_params(action: dict) -> dict | None:
    """Build ActionParams for /step from a legal action view.

    Returns None when the action requires targets (or X) we cannot supply.
    """
    params: dict = {}
    kind = action.get("kind", "")

    if kind == "DeclareAttackers" and action.get("validAttackers"):
        attacker = action["validAttackers"][0]
        defender = (action.get("validAttackTargets") or [None])[0]
        if defender:
            params["attackers"] = {attacker: defender}
        return params

    if kind not in ("CastSpell", "ActivateAbility"):
        return params

    min_targets = int(action.get("minTargets") or 0)
    if min_targets > 0:
        candidates = list(action.get("targetEntityIds") or [])
        if len(candidates) < min_targets:
            return None
        max_targets = int(action.get("maxTargets") or min_targets)
        take = min(max_targets, len(candidates))
        take = max(take, min_targets)
        params["targets"] = candidates[:take]

    if action.get("hasXCost"):
        max_x = action.get("maxAffordableX")
        params["xValue"] = int(max_x) if max_x is not None else 0

    return params


def pick_action(obs: dict) -> dict | None:
    actions = obs.get("legalActions") or []
    if not actions:
        return None

    priority = {
        "DECISION": -1,
        "PlayLand": 0,
        "CastSpell": 1,
        "ActivateAbility": 2,
        "DeclareAttackers": 3,
        "DeclareBlockers": 4,
        "PassPriority": 5,
    }

    def score(a: dict) -> tuple:
        kind = a.get("kind", "")
        affordable = 0 if a.get("affordable", True) else 2
        desc = (a.get("description") or "").lower()
        gs1_bonus = 0 if any(k.lower() in desc for k in DECK if k not in ("Forest", "Island", "Mountain")) else 1
        return (affordable, priority.get(kind, 9), gs1_bonus)

    sorted_actions = sorted(actions, key=score)
    for action in sorted_actions:
        if action.get("kind") == "DECISION":
            return {"actionId": action["actionId"]}
        params = build_step_params(action)
        if params is None:
            continue
        payload: dict = {"actionId": action["actionId"]}
        if params:
            payload["params"] = params
        return payload
    return None


def wait_for_server(timeout_s: float = 120.0) -> None:
    deadline = time.time() + timeout_s
    while time.time() < deadline:
        try:
            get("/health")
            return
        except Exception:
            time.sleep(2)
    raise SystemExit("gym-server did not become healthy in time")


def main() -> int:
    wait_for_server()
    created = post(
        "/envs",
        {
            "players": [
                {"name": "A", "deck": {"type": "Explicit", "cards": DECK}},
                {"name": "B", "deck": {"type": "Explicit", "cards": DECK}},
            ],
            "skipMulligans": True,
            "startingPlayerIndex": 0,
            "revealAll": True,
        },
    )
    env_id = created["envId"]
    obs = created["observation"]
    digests: list[str] = []
    loops = 0

    for step in range(MAX_STEPS):
        if obs.get("terminated"):
            print(
                json.dumps(
                    {
                        "status": "complete",
                        "steps": step,
                        "winnerId": obs.get("winnerId"),
                        "reason": obs.get("terminationReason"),
                    },
                    indent=2,
                )
            )
            return 0

        digest = obs.get("stateDigest")
        if digest and digests[-3:].count(digest) >= 2:
            loops += 1
            if loops > 5:
                print(json.dumps({"status": "stuck", "steps": step, "digest": digest}, indent=2))
                return 1
        else:
            loops = 0
        if digest:
            digests.append(digest)

        pending = obs.get("pendingDecision")
        if pending:
            if pending.get("requiresStructuredResponse"):
                response = build_structured_response(obs, pending)
                if not response:
                    print(
                        json.dumps(
                            {
                                "status": "unsupported_decision",
                                "steps": step,
                                "kind": pending.get("kind"),
                            },
                            indent=2,
                        )
                    )
                    return 1
                obs = post(f"/envs/{env_id}/decision", response)
                continue

            # Folded yes/no, number, mode, etc. appear as DECISION legalActions.
            for action in obs.get("legalActions") or []:
                if action.get("kind") == "DECISION":
                    obs = post(f"/envs/{env_id}/step", {"actionId": action["actionId"]})
                    break
            else:
                print(json.dumps({"status": "pending_no_action", "steps": step}, indent=2))
                return 1
            continue

        choice = pick_action(obs)
        if not choice:
            print(json.dumps({"status": "no_actions", "steps": step}, indent=2))
            return 1

        try:
            obs = post(f"/envs/{env_id}/step", choice)
        except urllib.error.HTTPError as e:
            body = e.read().decode()
            print(json.dumps({"status": "http_error", "steps": step, "error": body}, indent=2))
            return 1

    print(json.dumps({"status": "truncated", "steps": MAX_STEPS}, indent=2))
    return 1


if __name__ == "__main__":
    sys.exit(main())
