#!/usr/bin/bash
set -x
java -jar crypter-boot.jar --private-key-file=private.key --zip-file-name=bundles.zip --target=bundles decrypt
set +x

