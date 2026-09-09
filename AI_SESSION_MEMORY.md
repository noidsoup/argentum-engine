# AI Session Memory

Dated log of what the AI did each session. **The AI writes this, not the human.**
Append a new entry at close-out. No secrets, no PII.

Format per entry:

```
## YYYY-MM-DD — <branch or topic>
- **Shipped:** what landed
- **Decisions:** what was chosen and why
- **State:** current status
- **Blocked / next:** concrete next steps
```

---

<!-- newest entries on top -->

## 2026-08-29 — Chance handoff prep
- **Shipped:** `docs/HANDOFF-CHANCE.md`, working `.verify.sh` (build + test-rules), updated `MEMORY.md`
- **Decisions:** Chance grinds PC2/DOM extras (compose only); GS1 stays captain-owned on dao-desk
- **State:** Engine `main` synced to origin; campaign exports refreshed (GS1 21/41, PC2 62/136)
- **Blocked / next:** Invite Chance as GitHub collaborator on both forks; install JDK 21 on this Mac (`brew install openjdk@21`)


## 2026-09-09 — close-out

- **Where:** argentum-engine · branch `main` · PR none
- **Done:** Resolved Avacyn's Judgment feature merge on engine main; marked F-GRIND-AVACYN-S-JUDGMENT proven in campaign FEATURES.md; verified grind shift summary harness (c02b467) on campaign main
- **State:** VOC 145/150 — grind stopped, not running; 1 compose-ready card (Avacyn's Judgment), 4 feature-blocked
- **Decisions:** Kept HEAD madness docs (Shadowgrange Archfiend) in merge conflict; Avacyn feature ships before card
- **Risks:** `just build` has unrelated game-server/rules-engine test failures; campaign FEATURES/VOC edits may be local-only until committed
- **Next:** Commit campaign queue updates; restart VOC grind (~2h) to ship Avacyn's Judgment then feature-pivot remaining 4

