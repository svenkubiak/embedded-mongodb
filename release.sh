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
    mvn release:update-versions
  fi
fi

rm pom.xml.versionsBackup


