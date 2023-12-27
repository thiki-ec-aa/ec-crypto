#!/usr/bin/bash

## install crypter in bash

if [ "$1" = '-f' ] ;then
  rm ~/.crypter -rf
else
  if [ -d ~/.crypter ]; then
    echo "The directory '~/.crypter' exists. If you want to reinstall crypter, please remove '~/.crypter' first."
    exit 1
  fi
fi

mkdir ~/.crypter
cp ../crypter-boot.jar ~/.crypter/
cp ./crypter-function.sh ~/.crypter/

cat << EOT >> ~/.bashrc
crypter()
{
  java -jar ~/.crypter/crypter-boot.jar \$@
}
EOT

echo crypter installed. please run 'source ~/.bashrc' to take effect.
