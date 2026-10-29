#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [[ -f .env ]]; then
    printf '.env already exists; keeping the current configuration.\n'
    exit 0
fi
umask 077
password=$(od -An -N24 -tx1 /dev/urandom | tr -d ' \n')
printf 'POSTGRES_DB=student_api\nPOSTGRES_USER=student_api\nPOSTGRES_PASSWORD=%s\nAPP_PORT=8083\nIMAGE_TAG=v1.0.1-sarsembayev-eldar-37006\n' "$password" > .env
printf 'Created a private local .env. Password was not printed.\n'
