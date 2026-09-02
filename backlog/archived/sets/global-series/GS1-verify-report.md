# GS1 verify-set report — 2026-09-02

```
Set:              GS1 — Global Series: Jiang Yanggu & Mu Yanling
Completeness:     40/40 extras (`scripts/card-status`); 30 definitions + 6 reprint rows + 4 basics
Field fidelity:   40 cards vs Scryfall — 0 discrepancies (Gs1CardFieldVerificationTest, session)
Behaviour:        18 scenario tests on authored/tricky cards; reprints/simple bodies rely on corpus;
                  self-play: Gs1SelfPlaySmokeTest (SelfPlayLoop + HeuristicEvaluator) — full game, no stall
DSL fidelity:     assay-differential: 0 divergent of 22 read whole (8 declined grammar)
Tokens:           Jiang −1 creates Mowu token inline; no set token-art gaps
Waived:           none
Gate:             just test-class (GS1 scenarios) + test-gym-trainer — green; rebless-cards — green
```
