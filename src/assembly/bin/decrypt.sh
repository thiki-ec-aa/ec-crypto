#!/usr/bin/bash
set -x
keyfile=hxlc-xc.key

datestr=$1

zipfile=xc-v1.1.4-$datestr.zip

OIFS=$IFS
IFS=$'\n'
echo "enter the password:"
read -s passwd
IFS=$OIFS

unzip -P $passwd ${keyfile}.zip
java -jar crypter-boot.jar --private-key-file=${keyfile} --zip-file-name="${zipfile}" --target=$datestr decrypt
set +x

# clearning
rm $keyfile
rm "$datestr/*.zip" "$datestr/token.txt"
mv $datestr/$datestr/* $datestr
rmdir $datestr/$datestr

echo "Press any key to continue"
read -n 1

