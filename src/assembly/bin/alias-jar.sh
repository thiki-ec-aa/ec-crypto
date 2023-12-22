#!/usr/bin/bash

# run `source alias-jar.sh`
base=$(cd $(dirname ${BASH_SOURCE[0]});pwd)
echo base=$base

crypter()
{
  java -jar "${base}"/../crypter-boot.jar $@
}

echo function crypter defined.
