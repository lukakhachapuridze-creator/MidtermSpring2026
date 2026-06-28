# Database

## Overview

The game uses JPA (Hibernate) with an embedded H2 file database. Data is stored under `./data/uno` by default.

Persistence is enabled by default. Use `--no-db` to skip saving.

## Schema

| Table | Purpose |
|-------|---------|
| `players` | One row per player name. Tracks human/bot flag, career points, and wins. |
| `games` | One row per play session (a CLI run with a seed). |
| `rounds` | One row per completed round. Links to a game and winner. |
| `round_scores` | One row per player per round. Winner gets round points, others get 0. |

### Relationships

- A `game` has many `rounds`
- A `round` has many `round_scores`
- Each `round_score` links one `player` to one `round`

## Setup

No external database install is required. Hibernate creates and updates tables on startup (`hibernate.hbm2ddl.auto=update`).

Run a few games to populate data:

```bash
./mvnw package
java -jar target/midterm-uno-cli-1.0-SNAPSHOT.jar --bots 3 --games 2 --quiet --seed 42
```

The database files appear in `data/`.

## Tests

Persistence tests use an in-memory H2 database and run with Maven:

```bash
./mvnw test
```

Reports are written to `target/surefire-reports/`.

Tests cover:

- saving all participating players
- per-player round scores
- win counts and top scores
- recent game sessions

## Report Commands

Recent rounds:

```bash
java -jar target/midterm-uno-cli-1.0-SNAPSHOT.jar --history
```

Recent game sessions:

```bash
java -jar target/midterm-uno-cli-1.0-SNAPSHOT.jar --recent-games
```

Player win counts:

```bash
java -jar target/midterm-uno-cli-1.0-SNAPSHOT.jar --wins
```

Highest career scores:

```bash
java -jar target/midterm-uno-cli-1.0-SNAPSHOT.jar --top-scores
```

`--history` also prints all-time standings after the round list.

## Docker

Mount a volume to keep data between container runs:

```bash
docker build -t midterm-uno-cli .
docker run --rm -v uno-data:/app/data midterm-uno-cli --bots 3 --games 2 --quiet
docker run --rm -v uno-data:/app/data midterm-uno-cli --top-scores
```

## Configuration

Connection settings are in `resources/META-INF/persistence.xml`.

Entity classes live in `src/persistence/`. Save and query logic is in `PersistenceService`.
