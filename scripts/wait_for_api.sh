#!/usr/bin/env bash
set -euo pipefail
url=${1:-http://127.0.0.1:8083/actuator/health}
for attempt in $(seq 1 90); do
    if curl --silent --fail "$url" | grep -q '"UP"'; then
        printf 'PASS: API is ready at %s\n' "$url"
        exit 0
    fi
    sleep 2
done
printf 'FAIL: API did not become ready\n' >&2
exit 1
