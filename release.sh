#!/bin/bash
set -euo pipefail

# Check git state ONCE, at script start
check_clean_git() {
  if ! git diff-index --quiet HEAD --; then
    echo "There are uncommitted changes in the repository. Please commit or stash them before running this script."
    exit 1
  fi
}

# Official SemVer 2.0.0 regex, single line, bash-friendly
SEMVER_REGEX='^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(-((0|[1-9][0-9]*|[0-9]*[A-Za-z-][0-9A-Za-z-]*)(\.(0|[1-9][0-9]*|[0-9]*[A-Za-z-][0-9A-Za-z-]*))*))?(\+[0-9A-Za-z-]+(\.[0-9A-Za-z-]+)*)?$'

check_clean_git

# 1) Initial build
mvn clean verify

# 2) Get current version and prompt for new version
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
read -rp "Enter new version (current: ${CURRENT_VERSION}): " NEW_VERSION

# 3) Validate against strict SemVer 2.0.0
if [[ ! "$NEW_VERSION" =~ $SEMVER_REGEX ]]; then
  echo "Invalid Semantic Version (must follow SemVer 2.0.0): $NEW_VERSION"
  exit 1
fi

# 4) Set version (this will make the repo dirty, which is OK now)
mvn versions:set -DnewVersion="$NEW_VERSION"
STATUS=$?

VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

if [ $STATUS -ne 0 ]; then
  echo "Failed to set new version!"
else
  # 5) Release build
  mvn clean deploy -Prelease
  STATUS=$?
  if [ $STATUS -ne 0 ]; then
    echo "Failed to release!"
  else
    git tag "$VERSION"
    mvn release:update-versions
    git commit -am "Updated version after release"
    git push origin main
    git push origin "$VERSION"
  fi
fi

rm -f pom.xml.versionsBackup
