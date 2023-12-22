#!/usr/bin/bash
set -x

base=$(cd $(dirname $0);pwd)
source "$base"/alias-jar.sh

keyfile=hxlc-xc.key
datestr=$1
zipfile=xc-v1.1.4-$datestr.zip

#OIFS=$IFS
#IFS=$'\n'
#echo "enter the password:"
#read -s passwd
#IFS=$OIFS

crypter decrypt \
             --target="$datestr" \
             -pr="${keyfile}" \
             --zip-file="$zipfile"

#             "--password=hellokitty",

set +x

# clearning
#rm $keyfile
#rm "$datestr/*.zip" "$datestr/token.txt"
#mv $datestr/$datestr/* $datestr
#rmdir $datestr/$datestr

echo "Press any key to continue"
read -n 1

