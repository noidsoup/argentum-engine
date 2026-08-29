# Handoff — Chance (card coverage worker)

**Last updated:** 2026-08-29 · **Captain:** Nicholas (`noidsoup`)

This doc is the fast on-ramp for grinding missing Magic cards into Argentum. You do **not** need Nicholas's Mac or his `.env` secrets.

---

## What you're joining

Argentum Engine is an open-source MTG rules engine ([upstream](https://github.com/wingedsheep/argentum-engine)). Nicholas's fork is the working copy for a **card catalog campaign**: implement missing cards in small batches, verify them, open small PRs upstream when ready.

**Your lane:** compose/print cards only — no new engine features, no AUTOGEN dumps. If a card needs a new SDK primitive, flag it in the campaign `FEATURES.md` and pick a different card.

---

## Two repos (clone both as siblings)

| Repo | URL | Purpose |
|------|-----|---------|
| **Engine** | https://github.com/noidsoup/argentum-engine | Kotlin card defs, tests, engine |
| **Campaign** | https://github.com/noidsoup/argentum-tandem-coverage | Queue, claims, grind scripts, coordination |

Suggested layout:

```text
~/Projects/argentum-engine
~/Projects/argentum-tandem-coverage
```

Open **argentum-tandem-coverage** in Cursor or Claude Code. Say:

> **set up and grind**

The agent runs setup — you should not need a manual checklist. Full worker instructions: [`argentum-tandem-coverage/docs/ONBOARD-WORKER.md`](https://github.com/noidsoup/argentum-tandem-coverage/blob/main/docs/ONBOARD-WORKER.md).

---

## One-time machine setup

### 1. JDK 21 (required)

The engine uses Gradle toolchains for **JDK 21**. JDK 17 is not enough.

```bash
brew install openjdk@21
export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
# Or: export JAVA_HOME="$(/usr/libexec/java_home -v 21)"  when macOS registers the JDK
echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"' >> ~/.zshrc
java -version   # should show 21
```

### 2. Tooling

```bash
brew install just
```

Node 18+ only if you touch the web client (you usually won't for card work).

### 3. Engine env (optional for card grinding)

```bash
cd ~/Projects/argentum-engine
just init          # copies .env.example → .env
```

Card work does not need Redis, Postgres, or LLM keys.

### 4. Campaign worker identity

In `argentum-tandem-coverage`, create `.env` (never commit):

```bash
ARGENTUM_WORKER=chance-gpu
```

Second agent on the same box → the slot script assigns `chance-gpu-2`, etc.

---

## Daily loop (agent-owned)

```bash
cd ~/Projects/argentum-tandem-coverage
export ARGENTUM_WORKER=$(python3 scripts/claim-agent-slot.py --print)
just shift-dry              # smoke: join a set, one dry cycle
just shift 8                # unattended 8-hour grind (needs Cursor agent auth)
```

**Chat grind (manual agent):**

1. `just grind PC2` — see what's missing
2. `just suggest PC2 5` — next five cards (printing-first)
3. `just claim PC2 <cards…>` — lock cards in `CLAIMED.md`
4. In **engine**: branch `cards/pc2-extra-<your-worker>`, use `add-card` skill
5. `just hygiene` + `just build` / scenario test in engine
6. `just claim-done …` when batch lands

**Rules:**

- One worker slot per agent session (`claim-agent-slot`)
- Card claims in `CLAIMED.md` are the real mutex — shared sets are OK
- Branch: `cards/<set>-extra-<worker>` (never commit on someone else's branch)
- **Do not** auto-join captain sets (GS1 on `dao-desk`) — Nicholas owns features there
- Verify before done — paste real `just build` / test output

---

## Where to work (assigned lane)

**You are pinned to PC2 (Planechase 2012) extras** — compose/print only on branch `cards/pc2-extra-chance-gpu`.

```bash
cd ~/Projects/argentum-tandem-coverage
export ARGENTUM_WORKER=chance-gpu
just worker-join PC2 compose
# or: just shift PC2 8
```

| Set | Extra progress | Your lane? | Notes |
|-----|----------------|------------|-------|
| **PC2** | **62 / 136** done | **Yes — primary** | Skip FEATURES-blocked cards (umbra, living weapon, planar die, …) |
| **DOM** | 11 left | Backup | Small finishable drain if PC2 pool is blocked |
| **GS1** | 21 / 41 | **No** | Captain lane (`dao-desk`) — features + hard cards |
| **DDQ** | **71 / 71** | N/A | **Complete** — no work left |

Refresh counts:

```bash
cd ~/Projects/argentum-engine && scripts/card-status --set PC2
cd ~/Projects/argentum-tandem-coverage && just export PC2
```

Tier queue: `docs/queue/SET-PRIORITY.md` in the campaign repo.

---

## Fork ↔ upstream

| Remote | URL | Who merges |
|--------|-----|------------|
| `origin` | `noidsoup/argentum-engine` | Nicholas / Chance (`chance-nelson`, write access) |
| `upstream` | `wingedsheep/argentum-engine` | **Humans only** — open PRs from the fork |

Never push to `upstream`. Never force-push `main`.

When a batch is green: open a small PR to wingedsheep from the fork. See campaign `docs/UPSTREAM-NOTES.md`.

---

## Key engine docs (read before features)

| Doc | When |
|-----|------|
| [`AGENTS.md`](../AGENTS.md) | Always — hard rules, `just` recipes, multi-agent Gradle lock |
| [`docs/card-sdk-language-reference.md`](card-sdk-language-reference.md) | Any SDK touch |
| [`.claude/skills/add-card/SKILL.md`](../.claude/skills/add-card/SKILL.md) | Implementing a card |
| [`docs/architecture-principles.md`](architecture-principles.md) | Engine changes (captain only) |
| [`docs/gym-self-play-testing.md`](gym-self-play-testing.md) | Shake out broken cards |

**Agent skills in engine:** `add-card`, `add-random-card`, `add-feature` (captain), `generate-scenario`.

---

## Verify gate

```bash
cd ~/Projects/argentum-engine
./.verify.sh        # just build + just test-rules
just test           # full suite before upstream PR
```

Requires JDK 21 and network for first Gradle toolchain resolve.

---

## LanceDB doc search (optional)

Local semantic search over repo docs (agent tooling only):

```bash
python3 -m pip install -r requirements-lancedb.txt
python3 -u scripts/index_project_knowledge_lancedb.py --apply
python3 -u scripts/search_project_knowledge_lancedb.py "how do continuations work"
```

Index lands in `uncommitted/lancedb_project_knowledge/` (gitignored).

---

## Getting unstuck

| Problem | Fix |
|---------|-----|
| `Cannot find Java 21` | Install OpenJDK 21, set `JAVA_HOME` |
| Gradle hangs / OOM with multiple agents | Use `just build` not raw `./gradlew` — global lock serializes heavy runs |
| Card needs new effect type | Skip; log in campaign `docs/queue/FEATURES.md` |
| Claim conflict | `just worker-list`; pick unclaimed cards via `just suggest` |
| Set already has another worker | Normal for compose sets — stay apart via card claims |

**Captain:** Nicholas — Discord/playtesting context optional; campaign board + GitHub issues are enough for factory work.

---

## Current `main` snapshot

- Engine `main` @ `origin/main` (synced 2026-08-29)
- Recent work: PC2 Extra batches, GS1 Extra partial, agent hygiene (LanceDB index, `.verify.sh`, Claude rules)
- Stale local branches (`cards/gs1-extra-01`, old PC2 branches) were pruned 2026-08-29 — **branch fresh from `main`** for new work
