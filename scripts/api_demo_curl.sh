#!/usr/bin/env bash
set -euo pipefail
base=${API_URL:-http://127.0.0.1:8083}/api/sarsembayev-eldar/students
tmp=$(mktemp -d)
id=''
cleanup() {
    if [[ -n "$id" ]]; then
        curl --silent --show-error --max-time 10 -X DELETE "$base/$id" > /dev/null || true
    fi
    rm -f "$tmp/headers" "$tmp/body"
    rmdir "$tmp"
}
trap cleanup EXIT
request() {
    local method=$1 url=$2 expected=$3
    local args=(--silent --show-error --connect-timeout 5 --max-time 20 -D "$tmp/headers" -o "$tmp/body" -w '%{http_code}' -X "$method")
    if [[ $# -eq 4 ]]; then args+=(-H 'Content-Type: application/json' --data "$4"); fi
    printf '\n%s %s\n' "$method" "$url"
    local code
    code=$(curl "${args[@]}" "$url")
    cat "$tmp/headers" "$tmp/body"
    printf '\n'
    if [[ "$code" != "$expected" ]]; then
        printf 'FAIL: expected HTTP %s, received %s\n' "$expected" "$code" >&2
        return 1
    fi
}
email="eldar.37006.$(python3 -c 'import uuid; print(uuid.uuid4().hex)')@example.com"
data="{\"firstName\":\"Eldar\",\"lastName\":\"Sarsembayev\",\"email\":\"$email\",\"groupName\":\"IT2-2301\"}"
printf 'Student: Eldar Sarsembayev | IT2-2301 | 37006\n'
request POST "$base" 201 "$data"
id=$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1]))["id"])' "$tmp/body")
request GET "$base" 200
python3 -c 'import json,sys; rows=json.load(open(sys.argv[1])); sys.exit(0 if any(s["id"]==int(sys.argv[2]) for s in rows) else "Created student missing from list")' "$tmp/body" "$id"
request GET "$base/$id" 200
python3 -c 'import json,sys; s=json.load(open(sys.argv[1])); sys.exit(0 if s["id"]==int(sys.argv[2]) and s["lastName"]=="Sarsembayev" else "Wrong student response")' "$tmp/body" "$id"
updated=${data/IT2-2301/IT2-2301-UPDATED}
request PUT "$base/$id" 200 "$updated"
python3 -c 'import json,sys; s=json.load(open(sys.argv[1])); sys.exit(0 if s["groupName"]=="IT2-2301-UPDATED" else "PUT did not return the updated group")' "$tmp/body"
request GET "$base/$id" 200
python3 -c 'import json,sys; s=json.load(open(sys.argv[1])); sys.exit(0 if s["groupName"]=="IT2-2301-UPDATED" else "Update was not persisted")' "$tmp/body"
request DELETE "$base/$id" 204
deleted_id=$id
id=''
request GET "$base/$deleted_id" 404
printf '\nPASS: curl create/list/get/update/delete/not-found demonstration\n'
