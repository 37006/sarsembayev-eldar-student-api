#!/usr/bin/env python3
"""Checks real HTTP responses without third-party Python packages."""
import json
import os
import urllib.request
import urllib.error

BASE = os.environ.get('API_URL', 'http://127.0.0.1:8083')
PATH = '/api/sarsembayev-eldar/students'

def request(method, path, body=None, expected=200):
    data = None if body is None else json.dumps(body).encode()
    req = urllib.request.Request(BASE + path, data=data, method=method,
                                 headers={'Content-Type': 'application/json'})
    try:
        response = urllib.request.urlopen(req, timeout=20)
    except urllib.error.HTTPError as error:
        response = error
    with response:
        status, raw = response.status, response.read().decode()
        print(f'{method} {path} -> {status}\n{raw}')
        assert status == expected, (method, path, expected, status, raw)
        return json.loads(raw) if raw else None

def main():
    from uuid import uuid4
    sample = {'firstName': 'Eldar', 'lastName': 'Sarsembayev',
              'email': f'eldar.{uuid4().hex[:10]}@example.com', 'groupName': 'IT2-2301'}
    request('GET', '/')
    request('GET', '/actuator/health')
    student = request('POST', PATH, sample, 201)
    item = PATH + '/' + str(student['id'])
    try:
        assert any(s['id'] == student['id'] for s in request('GET', PATH))
        assert request('GET', item)['lastName'] == 'Sarsembayev'
        updated = dict(sample, groupName='IT2-2301-UPDATED')
        assert request('PUT', item, updated)['groupName'] == updated['groupName']
        assert request('GET', item)['groupName'] == updated['groupName']
        request('POST', PATH, sample, 409)
        request('POST', PATH, dict(sample, email='invalid'), 400)
        request('PUT', PATH + '/999999999', sample, 404)
        request('GET', PATH + '/abc', expected=400)
    finally:
        request('DELETE', item, expected=204)
    request('GET', item, expected=404)
    request('DELETE', item, expected=404)
    print('PASS: real PostgreSQL CRUD and error responses')

if __name__ == '__main__':
    main()
