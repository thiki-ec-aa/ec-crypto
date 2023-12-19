#!/usr/bin/bash
set -x
java -jar crypter-boot.jar --public-key-file=public.key --private-key-file=private.key init-keys
set +x

