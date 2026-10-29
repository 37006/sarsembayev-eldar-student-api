# Sarsembayev Eldar Student API

Eldar Sarsembayev | IT2-2301 | Student ID 37006 | DevOps Midterm

Project status: CRUD implemented and API usage documented by Eldar Sarsembayev.

This project manages student records through a Spring Boot REST API and stores them in PostgreSQL. It is a separate project from my earlier Python laboratory work.

## Requirements

Java 21, Bash, Git, Docker Engine and Docker Compose. The Gradle wrapper downloads Gradle 8.14.3. Jenkins needs Java 21 and a Linux agent with access to Docker.

## Run

```bash
bash scripts/configure_env.sh
docker compose up -d --build
curl http://127.0.0.1:8083/
curl http://127.0.0.1:8083/api/sarsembayev-eldar/students
```

The personal API URL is `http://127.0.0.1:8083/api/sarsembayev-eldar/students`. The database is addressed as `db:5432` inside Docker. Its port is not published to Windows.

## Tests

```bash
./gradlew clean test bootJar
python3 scripts/api_check.py
```

The Gradle tests use an isolated H2 database in PostgreSQL compatibility mode. The separate API check exercises the running application with real PostgreSQL. Passing H2 tests alone does not verify PostgreSQL or container persistence.

## Credentials

`.env` contains the local database password and is ignored by Git and Docker. `.env.example` contains field names only. Passwords must not be committed: Git retains old versions, and deleting the current file does not remove a previously published secret. Use Jenkins Credentials when authentication is required. A public GitHub checkout needs no GitHub password.

## Linux redirection

`>` creates a file or replaces its contents; `>>` appends. The report script replaces its own latest system report on each run.

## CI and deployment

Continuous Integration checks each change by building and testing it. Continuous Delivery keeps a verified version ready for an approved release. Continuous Deployment releases successful changes automatically. This Midterm pipeline performs CI and image verification; automated deployment is not required.

## CRUD feature by Eldar Sarsembayev

I added PUT updates for existing students. A missing student returns 404, invalid fields return 400, and an email already used by another student returns 409.

The CRUD feature includes automated checks for successful updates, missing IDs, invalid updates, deletion and email normalization.

## Project files and evidence

- `docs/API.md`: endpoints, request format and curl examples.
- `docs/REPRODUCE.md`: Linux, Docker and Jenkins reproduction instructions.
- `docs/REPORT.md`: requirement-by-requirement results and evidence references.
- `evidence/`: real command transcripts, API responses and Jenkins records.

No separate PDF report or mandatory video is specified in this Midterm task. The code, GitHub history and actual demonstrations are the primary results.

## Automatic CI verification

The Jenkins job `Sarsembayev_Eldar_Student_API` checks GitHub once per minute using Poll SCM. After a commit reaches main, Jenkins checks out that revision and runs all five stages. The build cause distinguishes an SCM-triggered run from a manual Build Now run. The PostgreSQL smoke check also verifies case-insensitive email uniqueness and rollback when an update would reuse another student's email.

## Reviewed release 1.0.2

The full suite now contains 31 tests. The review fixed scalar-to-string conversion, empty values created by Unicode whitespace trimming, and misleading duplicate-email errors. Normalization now happens before validation. The live regression command is `python3 scripts/review_api_check.py`; it also checks concurrent uniqueness against PostgreSQL. These checks remain enabled when Python runs with `-O`.

The main Jenkins job builds the release image. Other jobs use separate `ci-...-37006` tags so that a test branch cannot overwrite it. `scripts/api_demo_curl.sh` verifies every expected HTTP code, checks the updated data and cleans up its own student record. The supplied JAR in the submission archive is a build artifact; Docker Compose is the documented way to run it with PostgreSQL.
