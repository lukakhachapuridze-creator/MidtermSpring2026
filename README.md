# Midterm UNO CLI

This is a standalone CLI UNO-like game built with Maven.

Gameplay lives in `service/GameService` with rules in `service/CardRulesService`, persistence in `persistence/`, and CLI wiring in `Main`. Round results are saved to an H2 database by default.

## Compile

```bash
scripts/compile.sh
```

Or:

```bash
./mvnw clean compile
```

## Run Bot Games

```bash
scripts/run.sh --bots 3 --games 5 --quiet
```

Replay a session with a fixed seed:

```bash
scripts/run.sh --bots 3 --seed 42
```

## Run Interactive Game

```bash
scripts/run.sh --human --bots 2 --games 1
```

Set a custom player name:

```bash
scripts/run.sh --name Luka --bots 2
```

`--name` adds a human player when `--human` is not passed. Use `--humans N` for more than one human.

Card input examples:

```text
R5   red 5
YS   yellow skip
BR   blue reverse
G+2  green draw two
W    wild
W4   wild draw four
draw draw a card
uno  call UNO at one card
```

During play you may also be prompted for color calls, playing a drawn card, Wild Draw Four challenges, jump-in, and seven-zero hand swaps.

## Optional Rules

House rules are off by default except where noted. Enable them with CLI flags:

| Flag | Effect |
|------|--------|
| `--target N` | Stop when a player reaches N total points |
| `--no-stack` | Disable draw stacking (+2 on +2, W4 on +2/W4) |
| `--no-challenge` | Disable Wild Draw Four challenges |
| `--no-uno-penalty` | Disable penalty for forgetting UNO |
| `--no-opening-effect` | Disable opening Skip, Reverse, or Draw Two |
| `--seven-zero` | 7 swaps hands; 0 rotates all hands |
| `--jump-in` | Play an exact duplicate card out of turn |

Draw stacking, Wild Draw Four challenges, Wild Draw Four color restriction, UNO penalty, and opening-card effects are on by default.

Example with house rules and a target score:

```bash
scripts/run.sh --human --bots 2 --seven-zero --jump-in --target 500
```

## CLI Options

| Flag | Default | Description |
|------|---------|-------------|
| `--bots N` | 3 | Number of bot players |
| `--games N` | 1 | Number of rounds to play |
| `--human` | off | Add one human player |
| `--humans N` | 0 | Number of human players |
| `--name NAME` | You | Name of the first human player |
| `--quiet` | off | Less console output |
| `--seed N` | current time | Random seed |
| `--no-db` | off | Skip database persistence |
| `--help` | | Print usage |

## Database Reports

Round results are stored in `./data/uno.mv.db` unless `--no-db` is used.

```bash
scripts/run.sh --history
scripts/run.sh --recent-games
scripts/run.sh --wins
scripts/run.sh --top-scores
```

See `docs/database.md` for schema details.

## Tests

Characterization checks:

```bash
scripts/test.sh
```

JUnit tests and characterization:

```bash
./mvnw test
```

Run the packaged self-test:

```bash
./mvnw package
java -jar target/midterm-uno-cli-1.0-SNAPSHOT.jar --self-test
```

## Docker

```bash
docker build -t midterm-uno .
docker run -it --rm -v uno-data:/app/data midterm-uno --human --bots 2
```

## Submission

Submit your work through GitHub:

1. Fork this repository to your GitHub account.
2. Clone your fork locally.
3. Complete the midterm work in your fork.
4. Commit your changes with clear commit messages.
5. Push your branch to GitHub.
6. Open a pull request from your fork back to the original repository.

Your pull request must include:

* refactored source code
* characterization tests
* `docs/refactoring-report.md`
* `docs/extension-readiness.md`

Do not submit a zip file instead of a pull request unless the instructor explicitly asks for it.

## Rules

See `docs/rules.html` for the implemented game rules.

## Midterm Materials

* `docs/midterm-exam.md`: midterm brief
* `docs/rubric.md`: grading rubric
* `docs/refactoring-guide.md`: suggested refactoring path
