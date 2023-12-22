#!/usr/bin/bash
set -x

base=$(cd $(dirname $0);pwd)
source "$base"/alias-jar.sh

keyfile=hxlc-xc.key
datestr=$1
zipfile=xc-v1.1.4-$datestr.zip

crypter decrypt \
             --target="$datestr" \
             -pr="${keyfile}" \
             --zip-file="$zipfile" \
             --password
set +x
echo "Press any key to continue"
read -n 1

