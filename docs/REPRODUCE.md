# Reproduce the Midterm

Eldar Sarsembayev, IT2-2301, 37006.

## Local Linux environment

Environment: Ubuntu 24.04 in WSL 2, distribution `DevOps-Backup-20261011`. Project directory: `/home/eldar/devops/sarsembayev-eldar-student-api`. Docker Engine runs inside Linux.

```powershell
wsl -d DevOps-Backup-20261011 -u eldar
```

```bash
cd ~/devops/sarsembayev-eldar-student-api
java -version
javac -version
docker version
docker compose version
./gradlew clean test bootJar
bash scripts/configure_env.sh
docker compose up -d --build
bash scripts/wait_for_api.sh
python3 scripts/api_check.py
```

On a different Linux machine, clone the GitHub repository into a new directory and use the same Bash commands. Java 21 must include `javac`. Use the checked-in Gradle wrapper, whose distribution SHA-256 is pinned. The first build downloads Gradle, Maven dependencies and Docker images. BuildKit caches subsequent Gradle downloads inside the Docker build.

The application listens at `127.0.0.1:8083`; the existing Jenkins is at `127.0.0.1:8080`. The PostgreSQL container has no published host port. Local services are not exposed to the public internet.

## Linux evidence

```bash
bash scripts/linux_evidence.sh
ls -l scripts/Sarsembayev_Eldar_system_report.sh
tail -n 5 logs/Sarsembayev_Eldar_system_report.log
cat evidence/Sarsembayev_Eldar_log_analysis.txt
cmp README.md backups/Sarsembayev_Eldar_README_backup.md
```

The script is mode 744: the owner has read/write/execute, group and others have read only. The personal report log and backup are local generated files ignored by Git. Re-running the demonstration replaces the latest report and README backup.

## Docker management

```bash
docker compose ps
docker compose logs --tail 60 app
docker inspect sarsembayev-eldar-student-api-app --format '{{.Config.Image}} {{.Config.User}}'
docker compose exec app sh -c 'id; java -version'
docker compose stop app
docker compose start app
docker compose restart
```

For a persistence check, create a student, note its ID, run `docker compose down`, then `docker compose up -d` and retrieve the same ID. Do not use `down -v`: that explicitly removes the database volume and its data.

Avoid distributing raw `docker inspect` output or `docker compose config`: these can display the database password from `.env`. The evidence includes selected configuration fields instead.

## Jenkins

Create a Pipeline job named `Sarsembayev_Eldar_Student_API`. Select Pipeline script from SCM, Git, the project's GitHub HTTPS URL, branch `*/main`, Script Path `Jenkinsfile`. Use a Linux agent with Java 21 JDK, Git, Python-independent Gradle tests, and permission to use Docker. Public HTTPS checkout needs no GitHub password. For a private repository, store a scoped GitHub credential in Jenkins Credentials and select its ID in the job; never put a token in the repository URL or Jenkinsfile.

The five stages are Checkout, Build, Test, Docker Build and Verify. Failed Gradle commands fail the stage and skip later stages. Test XML is archived. Poll SCM runs once per minute for this lab; a webhook is an alternative if the controller is reachable securely. There is no deployment stage.

The troubleshooting job uses the personal `test/sarsembayev-eldar-pipeline` branch. Its intentionally incorrect test expectation is fixed on the same branch; the main branch remains usable.

## Inspect Git

```bash
git remote -v
git log --oneline --graph --all
git branch -a
git tag -n
git status --short
git check-ignore .env logs/Sarsembayev_Eldar_system_report.log backups/Sarsembayev_Eldar_README_backup.md
```

`evidence/git-workflow.txt` records the two conflicting README lines, their resolution and the resulting merge commit.
