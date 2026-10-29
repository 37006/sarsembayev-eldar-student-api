# DevOps Midterm

Eldar Sarsembayev | IT2-2301 | Student ID 37006

All required local and Jenkins demonstrations have recorded results.

## Application and environment

I implemented a student register with Spring Boot 3.5.16, Java 21 and Gradle 8.14.3. PostgreSQL 17 stores the records in a named Docker volume. The personal endpoint is `http://127.0.0.1:8083/api/sarsembayev-eldar/students`.

The application has a controller, a transactional service and a JPA repository. The database generates numeric IDs and enforces unique emails. Request validation rejects incomplete or malformed records. The responses are 201 for creation, 200 for retrieval and update, 204 for deletion, 400 for invalid input, 404 for a missing student and 409 for a duplicate email. The home endpoint identifies the student, group, ID and application version.

The project is separate from my previous Python laboratory. The local environment is the restored Ubuntu WSL distribution `DevOps-Backup-20261011`; Docker Engine and Jenkins run inside it. I installed the missing Java compiler before building this project. No result from the earlier Python application is presented as a Midterm test.

## Requirement checklist

| Task | Implementation and verification | Evidence |
|---|---|---|
| 1.1 | Created scripts, logs, backups and src with terminal commands; checked required files and the initially empty personal log | `linux-administration.txt` |
| 1.2 | Personal Bash script reports date/time, user, OS/kernel, CPU, memory and disk usage/available space; saves its output | `linux-administration.txt`, personal `.log` in the submission |
| 1.3 | Set report script mode 744; copied README to a personalized backup and compared the files | `linux-administration.txt`, `backups/` |
| 1.4 | Listed processes and Java processes; displayed five log lines; searched surname and saved the match | `linux-administration.txt`, `Sarsembayev_Eldar_log_analysis.txt` |
| 2.1 | Initialized main, configured Git identity, added .gitignore and made the required personalized initial commit | `git-workflow.txt` |
| 2.2 | Added PUT on the personal CRUD branch, updated README and made two meaningful feature commits before merging | `git-workflow.txt`, Git history |
| 2.3 | Changed the same README line on main and the documentation branch; resolved the actual conflict by editing the combined status | `git-workflow.txt` includes conflict markers and resolution |
| 2.4 | Personal public repository, remote branches and release tags; `.env` excluded throughout history | `git-remote.txt`, `final-release-git.txt`, `local-audit.txt` |
| 3.1 | Multi-stage official JDK/Java runtime build using the Gradle wrapper, non-root runtime, port 8080 and individual image tags | `docker-first-build.txt`, `version-rebuild-summary.txt` |
| 3.2 | Application and PostgreSQL services, environment settings, named volume, dedicated network and database health check; JDBC uses db, not localhost | `docker-first-build.txt`, `version-rebuild-summary.txt` |
| 3.3 | Listed containers, read logs, inspected selected config, ran commands in the app, stopped/started it, restarted both services, recreated containers, and rebuilt version 1.0.0 as 1.0.1 | `container-management-persistence.txt`, `docker-version-rebuild.txt` |
| 3.4 | Demonstrated create/list/get/update/delete and a nonexistent ID using curl and individual student data | `curl-crud.txt`; additional error checks in `api-postgresql-final.txt` |
| 4.1 | Required job and GitHub SCM checkout on a Java 21/Docker-capable Linux agent | Jenkins initial console and job configuration |
| 4.2 | Checkout, Build, Test, Docker Build and Verify; personal identity and image printed; failed commands stop later stages | `Jenkinsfile`, Jenkins console and stage records |
| 4.3 | Successful baseline, isolated failing test expectation, correction and successful rerun | Build #1: SUCCESS; Build #1: FAILURE; Build #2: SUCCESS |
| 4.4 | SCM polling, a new GitHub commit and an automatically triggered successful build | Build #2: SUCCESS; polling/build cause records |

Evidence filenames in the table are relative to `evidence/`. The final status of external tasks must be checked against the actual remote and Jenkins records, not inferred from the presence of configuration files.

## Verified application results

The final local Gradle run executed 31 tests with 0 failures and 0 errors. These tests use H2 in PostgreSQL compatibility mode so they can run independently in Jenkins. I separately checked the real PostgreSQL-backed application with HTTP requests; H2 results alone are not used as proof of PostgreSQL operation.

The persistence demonstration created a record containing my name, retained its generated ID and retrieved the same record after app stop/start, both-service restart, Compose down/up without deleting the volume, and rebuilding the application. The retained sample is identified in `persistence-student.json`. Other API smoke-test records are removed by their test scripts.

The first image is tagged `v1.0-sarsembayev-eldar-37006`. After changing the Gradle application version to 1.0.1, I rebuilt and ran `v1.0.1-sarsembayev-eldar-37006`; the home endpoint returned 1.0.1 and the stored record remained available. Image IDs, uncompressed sizes and creation times are recorded in the actual transcript.

## Git conflict and CI failure

The README conflict occurred because the documentation branch and main changed the same project-status line from their common ancestor. I retained both meanings in the resolved line: CRUD was implemented and API usage documented. The resolution is a merge commit with both parent branches.

The controlled CI error belongs only to `test/sarsembayev-eldar-pipeline`. It changes the expected response to an invalid email from 400 to 201. The application correctly returns 400, so the deliberately incorrect test fails. Correcting the expectation to 400 fixes the test. The main application does not need to be broken to demonstrate Pipeline failure.

The original main branch had 17 automated tests. Its isolated troubleshooting branch added one test: the recorded first run completed 18 tests with one deliberately failing assertion, and the corrected run completed successfully. The failed console explicitly records that Docker Build and Verify were skipped. These historical XML files are included under `evidence/jenkins-test-results/`. The reviewed release now has 31 main-branch tests; the current test branch additionally includes the corrected troubleshooting test.

The automatically triggered main build was started by `hudson.triggers.SCMTrigger$SCMTriggerCause`. Its saved result identifies the checked-out Git revision, and its console finishes with SUCCESS. This verifies the configured polling trigger through an actual GitHub change rather than a manual Build Now request.

## Credentials and service access

The local database password is generated into a mode-600 `.env`. It is excluded by Git and the Docker build context, and is not included in the submission archive. No Jenkins password or GitHub token belongs in the source files or evidence. Passwords must not be committed because Git preserves previous versions and other users or automated systems could retrieve them later.

The application is published only on loopback port 8083. PostgreSQL is available only on the dedicated Docker network. A successful database health check allows Compose to start the app; the HTTP readiness script then checks the application itself. A Docker network connects services; the named volume retains database files when containers are replaced.

## Review and corrected release 1.0.2

I repeated the specification check and tested inputs beyond the original happy path. Three input problems were reproduced against the running PostgreSQL-backed API: a JSON number was converted into a name, Unicode whitespace was trimmed into an empty value only after validation, and Unicode lowercasing could expand an email beyond the database field limit and produce a misleading duplicate-email response.

The request record now normalizes Unicode whitespace and email case before Bean Validation. Jackson rejects numbers and booleans where the JSON contract requires strings. Only a violation of the named email uniqueness constraint is reported as a duplicate email; an unrelated integrity error is a server error and does not claim that an email already exists.

The new regression tests were run before the fix: 10 of the 11 initial regression cases failed. After correction, the complete suite has 31 passing tests. PostgreSQL checks also cover invalid updates leaving the existing name unchanged and four concurrent requests for one email, which must produce one 201 and three 409 responses. The curl demonstration now verifies every expected HTTP status and the updated record instead of relying only on curl's HTTP-error handling. Its test email is random to avoid collisions between repeated runs, and its own record is cleaned up on exit.

The current application version is 1.0.2, with release image `sarsembayev-eldar-student-api:v1.0.2-sarsembayev-eldar-37006`. The original persistence record survived this upgrade and another service restart. The personal Linux demonstration explicitly empties its latest report log before regenerating it, so its displayed initial size is accurate on repeated runs.

Jenkins test jobs now use separate `ci-...-37006` image tags. They cannot overwrite the main release image tag. The historical failed/fixed Pipeline records above are retained, and the reviewed test branch is built again to verify this isolation.

Current review evidence is in `evidence/review/`: `input-before.jsonl`, `regressions-before.txt`, `gradle-tests.txt`, `docker-1.0.2-build.txt`, `api-postgresql.txt`, `regression-api.txt`, `curl-crud.txt`, `linux-administration.txt` and `persistence-and-version.txt`. The before-fix failures are historical regression evidence, not the current test result.

## Required explanations

- `>` replaces a file's contents; `>>` appends to it.
- Mode 744 means owner rwx, group r--, others r--. Mode 755, mentioned among the defense examples, would additionally allow group and others to execute and therefore does not meet Task 1.3.
- A Git branch is a movable reference to a commit; a release tag marks a particular version. Merge combines histories and may require a decision when edits overlap.
- A Docker image is the packaged filesystem and startup configuration; a container is a running or stopped instance. A multi-stage build keeps build tools out of the final Java runtime image.
- `EXPOSE 8080` documents the container port; the Compose port mapping actually publishes it on host port 8083.
- CI integrates, builds and tests changes. Continuous Delivery keeps verified changes ready for release with approval; Continuous Deployment releases them automatically. This task requires CI and image verification, not automated deployment.
- Jenkins checks out the source using Git, builds the Java application with its wrapper, runs tests, builds the personal image and verifies that the image exists. A nonzero Gradle result fails the stage and skips subsequent stages.

## Submission

Submit the personal GitHub repository URL and the final project archive containing source, Gradle wrapper, Git history, report, personal system log, README backup and real evidence. The supplied Midterm does not explicitly require a separate PDF report or a video. The instructor may ask for a live terminal or Jenkins demonstration.

Repository: https://github.com/37006/sarsembayev-eldar-student-api

Source revision when this report was generated: `c3e281f1cfa31cfedd40205383ecee4e048f75ab`. A later documentation-only commit may contain this report and the evidence itself.

## References

- Spring Boot 3.5 system requirements: https://docs.spring.io/spring-boot/3.5/system-requirements.html
- Docker Compose startup order and health checks: https://docs.docker.com/compose/how-tos/startup-order/
- Jenkins Declarative Pipeline syntax and triggers: https://www.jenkins.io/doc/book/pipeline/syntax/
- Git merge and conflict handling: https://git-scm.com/docs/git-merge
