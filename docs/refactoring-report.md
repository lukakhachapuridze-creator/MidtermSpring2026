# Refactoring Report

## Tests first

I ran `scripts/test.sh` and the old self test. Then I wrote `CharacterizationTest` for matching by color, number, skip/reverse, wilds, points, bot picks, and drawing when the deck runs out.

## What was wrong before

`Main` did everything. Legal play checks were duplicated in the game loop and in `chooseBotCard`. Input prompts were mixed with rules.

## What I did

- `CharacterizationTest` - extra checks
- `model/CardColor` and `CardRank` enums
- `service/` folder with interfaces and `*Service` classes for rules, deck, bots, console, game

`Main` just does args, players, and final scores now.

## Same behavior

Did not change the rules from `docs/rules.html`. Still same card strings, bot order (draw two then skip then number then wild), penalties, scoring, 3000 turn limit.

## Still messy

`Game` is still long. Effects are still if/else in `applyCardEffect`. Did not add replay. Most play testing is still running bot games.
