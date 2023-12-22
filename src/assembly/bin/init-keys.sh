#!/usr/bin/bash
set -x

base=$(cd $(dirname $0);pwd)
source "$base"/alias-jar.sh

crypter init-keys \
            -pu=public.key \
            -pr=private.key \
            --password
set +x

