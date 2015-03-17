#!/bin/bash

if [ -z "$1" ]; then
  echo Please provide URL of coral.jnlp for first param
  exit 1
fi

CORALTEMPDIR=/tmp/coral-jars-`date +%Y-%m-%d_%H_%M_%S`
mkdir -p $CORALTEMPDIR
cd $CORALTEMPDIR
wget $1
perl -ne '/codebase="(.*)"/ && ($BASE="$1"); /jar href="(.*)"/ && print "$BASE$1\n"' coral.jnlp > downloads.txt
wget -i downloads.txt
for i in *.jar; do
  FILEBASE=$(basename $i .jar);
  mvn install:install-file -Dfile=$i -DgroupId=org.opencoral -DartifactId=opencoral-$FILEBASE  -Dversion=3.4.9 -Dpackaging=jar
done
