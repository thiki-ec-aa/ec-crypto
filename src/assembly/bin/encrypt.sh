#!/usr/bin/bash
set -x
java -jar crypter-boot.jar --public-key-file=public.key --zip-file-name=bundles.zip --bundle-folder-name=bundles encrypt

set +x

