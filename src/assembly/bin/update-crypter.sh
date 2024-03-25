#!/usr/bin/bash

## update crypter jar
cd $(dirname $0)

if [ "$1" = '' ] ;then
    echo "need parameter to specify crypter install parent directory."
    exit 1
fi
jar_name=crypter-boot.jar
install_root=$base/.crypter

if [ ! -d "$install_root"  ] ; then
  mkdir "$install_root"
fi

cp -f ../${jar_name} "$install_root"/
cp -f ./crypter-function.sh "$install_root"/

echo crypter is updated.


