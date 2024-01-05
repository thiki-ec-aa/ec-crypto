#!/usr/bin/bash
set -x

source ~/.crypter/crypter-function.sh

# select the key file
keyfile=private.key.zip
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

