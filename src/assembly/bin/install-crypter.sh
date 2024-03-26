#!/usr/bin/bash

set +x
cd $(dirname $0)

## install crypter in bash
base=$1
jar_name=crypter-boot.jar
crypter_home=$base/.crypter

if [ "$2" = '-f' ] ;then
  rm $base/.crypter -rf
else
  if [ -d ${crypter_home} ]; then
    echo "The directory [${crypter_home}] exists. If you want to reinstall crypter, please remove [${crypter_home}] first."
    exit 1
  fi
fi

mkdir ${crypter_home}
cp ../${jar_name} ${crypter_home}/
cp ./crypter-function.sh ${crypter_home}/

cat << EOT >> ~/.bashrc
export crypter_home=$crypter_home
crypter()
{
  java -jar $crypter_home/crypter-boot.jar $@
}
EOT

echo crypter installed. please run 'source ~/.bashrc' to take effect, and run 'crypter -h' to verify.

set -x


