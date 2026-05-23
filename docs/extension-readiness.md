# Extension Readiness

## Easiest thing to add

A new card effect or small rule change. I would start in `CardRules` and `Game.applyCardEffect`.

## Where to edit

- play legality: `CardRules` / `CardRulesService`
- what happens after a card: `GameService.applyCardEffect`
- bot picks: `BotPlayer` / `BotPlayerService`
- deck contents: `DeckService.newShuffledDeck`

## Still annoying to change

Players and scores setup is split between `Main` and `Game`. A replay would need print statements pulled out of `Game` somehow. Smarter bots need more info in `BotPlayer` than just hand + up card.
