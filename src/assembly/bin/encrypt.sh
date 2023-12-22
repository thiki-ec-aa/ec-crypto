#!/usr/bin/bash
set -x
base=$(cd $(dirname $0);pwd)
source "$base"/alias-jar.sh

datestr=$1
keyfile=public.key
zipfile=xc-v1.1.4-$datestr.zip

crypter encrypt \
            -pu=$keyfile \
            --zip-file=$zipfile \
            --bundle-folder=$datestr

set +x

