#!/usr/bin/env bash
set -euo pipefail

before_tag="$(git describe --tags --abbrev=0 2>/dev/null || true)"

npx semantic-release

after_tag="$(git describe --tags --abbrev=0 2>/dev/null || true)"

if [[ -n "$after_tag" && "$after_tag" != "$before_tag" ]]; then
    version="${after_tag#v}"

    echo "new-release-published=true" >> "$GITHUB_OUTPUT"
    echo "release-version=$version" >> "$GITHUB_OUTPUT"

    echo "New release published: $version"
else
    echo "new-release-published=false" >> "$GITHUB_OUTPUT"
    echo "release-version=" >> "$GITHUB_OUTPUT"

    echo "No new release published"
fi