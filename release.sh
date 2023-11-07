#!/bin/bash
mvn versions:set
STATUS=$?
if [ $STATUS -ne 0 ]; then
  echo "Failed to set new version!"
else
  mvn clean deploy -Prelease
  STATUS=$?
  if [ $STATUS -ne 0 ]; then
    echo "Failed to release!"  
  else
    git tag $STATUS
    mvn release:update-versions
    git commit -am "Updated version after release"
    git push origin main
  fi
fi

rm pom.xml.versionsBackup
