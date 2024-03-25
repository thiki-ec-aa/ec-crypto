#!/usr/bin/bash

set +x
cd $(dirname $0)

## install crypter in bash
base=$1
jar_name=crypter-boot.jar
install_root=$base/.crypter

if [ "$2" = '-f' ] ;then
  rm $base/.crypter -rf
else
  if [ -d ${install_root} ]; then
    echo "The directory [${install_root}] exists. If you want to reinstall crypter, please remove [${install_root}] first."
    exit 1
  fi
fi

mkdir ${install_root}
cp ../${jar_name} ${install_root}/
cp ./crypter-function.sh ${install_root}/

cat << EOT >> ~/.bashrc
crypter()
{
  java -jar ~/.crypter/crypter-boot.jar \$@
}
EOT

echo crypter installed. please run 'source ~/.bashrc' to take effect, and run 'crypter -h' to verify.

set -x
