#!/usr/bin/env python3
"""Regression checks against the running PostgreSQL-backed API."""
from concurrent.futures import ThreadPoolExecutor
import json
import urllib.request
import urllib.error
from uuid import uuid4
from api_check import BASE, PATH, request, require

def main():
    sample = {'firstName': 'Eldar', 'lastName': 'Sarsembayev',
              'email': f'review.{uuid4().hex}@example.com', 'groupName': 'IT2-2301'}
    for invalid in [123, 12.5, True, '\u2003', '\u00a0\u202f']:
        request('POST', PATH, dict(sample, firstName=invalid), 400)
    expanded_email = '\u0130' * 64 + '@' + '.'.join(['a' * 60] * 3)
    request('POST', PATH, dict(sample, email=expanded_email), 400)
    padded = dict(sample, firstName='\u2003Eldar\u2003', email=' ' + sample['email'].upper() + ' ')
    student = request('POST', PATH, padded, 201)
    item = PATH + '/' + str(student['id'])
    try:
        require(student['firstName'] == 'Eldar' and student['email'] == sample['email'], 'Normalization failed')
        request('PUT', item, dict(sample, firstName='\u2003'), 400)
        require(request('GET', item)['firstName'] == 'Eldar', 'Invalid update erased the name')
    finally:
        request('DELETE', item, expected=204)

    concurrent = dict(sample, email=f'concurrent.{uuid4().hex}@example.com')
    def create_same_email(_):
        req = urllib.request.Request(BASE + PATH, data=json.dumps(concurrent).encode(), method='POST',
                                     headers={'Content-Type': 'application/json'})
        try:
            response = urllib.request.urlopen(req, timeout=20)
        except urllib.error.HTTPError as error:
            response = error
        with response:
            return response.status, json.loads(response.read())
    with ThreadPoolExecutor(max_workers=4) as pool:
        responses = list(pool.map(create_same_email, range(4)))
    created = [body for status, body in responses if status == 201]
    try:
        statuses = sorted(status for status, _ in responses)
        print('Concurrent duplicate-email responses:', statuses)
        require(statuses == [201, 409, 409, 409], 'Concurrent uniqueness or conflict handling failed')
    finally:
        for body in created:
            request('DELETE', PATH + '/' + str(body['id']), expected=204)
    print('PASS: strict JSON types, Unicode normalization, safe updates and concurrent PostgreSQL uniqueness')

if __name__ == '__main__':
    main()
