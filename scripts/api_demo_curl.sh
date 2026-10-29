#!/usr/bin/env bash
set -euo pipefail
base=${API_URL:-http://127.0.0.1:8083}/api/sarsembayev-eldar/students
tmp=$(mktemp -d)
trap 'rm -f "$tmp/headers" "$tmp/student"; rmdir "$tmp"' EXIT
email="eldar.37006.$(date +%s)@example.com"
data="{\"firstName\":\"Eldar\",\"lastName\":\"Sarsembayev\",\"email\":\"$email\",\"groupName\":\"IT2-2301\"}"
printf 'Student: Eldar Sarsembayev | IT2-2301 | 37006\n'
printf '\nPOST %s\n%s\n' "$base" "$data"
curl --silent --show-error --fail-with-body -D "$tmp/headers" -o "$tmp/student" -X POST "$base" -H 'Content-Type: application/json' --data "$data"
cat "$tmp/headers" "$tmp/student"
grep -q '201' "$tmp/headers"
id=$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1]))["id"])' "$tmp/student")
printf '\n\nGET %s\n' "$base"
curl --silent --show-error --fail-with-body -i "$base"
printf '\n\nGET %s/%s\n' "$base" "$id"
curl --silent --show-error --fail-with-body -i "$base/$id"
updated=${data/IT2-2301/IT2-2301-UPDATED}
printf '\n\nPUT %s/%s\n%s\n' "$base" "$id" "$updated"
curl --silent --show-error --fail-with-body -i -X PUT "$base/$id" -H 'Content-Type: application/json' --data "$updated"
printf '\n\nDELETE %s/%s\n' "$base" "$id"
code=$(curl --silent --show-error -D "$tmp/headers" -o /dev/null -w '%{http_code}' -X DELETE "$base/$id")
cat "$tmp/headers"
test "$code" = 204
printf '\nGET deleted student %s/%s\n' "$base" "$id"
code=$(curl --silent --show-error -i -w '\n%{http_code}' "$base/$id")
printf '%s\n' "$code"
test "${code##*$'\n'}" = 404
printf '\nPASS: curl create/list/get/update/delete/not-found demonstration\n'
