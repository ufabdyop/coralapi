#!/bin/bash
set -e

echo "bumping version"
POMVERSION=`ruby bumpPom.rb`

echo "deploy"
mvn -Dmaven.test.skip=true deploy

echo "commit"
git commit -a -m "Bumping to version $POMVERSION"

echo "tag"
git tag v$POMVERSION

echo "push"
git push

echo "push tag"
git push -u origin v$POMVERSION

echo "bumping version"
ruby bumpPom.rb

