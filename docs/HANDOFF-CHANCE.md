# Handoff — Chance (card coverage worker)

**Canonical handoff:** [`argentum-tandem-coverage/docs/HANDOFF-CHANCE.md`](https://github.com/noidsoup/argentum-tandem-coverage/blob/main/docs/HANDOFF-CHANCE.md) — queue, claims, daily loop, and current set targets live there only (avoids drift).

## Quick start

1. Clone **engine** + **campaign** as siblings under `~/Projects/`.
2. Open the **campaign** repo; follow [ONBOARD-WORKER.md](https://github.com/noidsoup/argentum-tandem-coverage/blob/main/docs/ONBOARD-WORKER.md) or say **set up and grind**.
3. Implement cards in **this** repo: `add-card` skill, `./.verify.sh`, merge to `origin/main` on the fork — never push `upstream`.

## Engine-only pointers

| Doc | When |
|-----|------|
| [`AGENTS.md`](../AGENTS.md) | Hard rules, `just` recipes |
| [`.claude/skills/add-card/SKILL.md`](../.claude/skills/add-card/SKILL.md) | Implementing a card |
| [`./.verify.sh`](../.verify.sh) | Pre-merge gate (JDK 21) |
