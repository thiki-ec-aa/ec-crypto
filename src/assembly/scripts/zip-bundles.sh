#!/bin/bash
set -x

target=""
while getopts d:t:k: flag
do
    case "${flag}" in
        d) datestr=${OPTARG};;
        t) target=${OPTARG};;
        k) key=${OPTARG};;
        *) echo 'invalid flag'; exit 1
    esac
done

if [ -z "$target" ]; then
  echo "The option target(-t) is absent, use default: '.'"
  target=.
fi
if [ -z "$datestr" ]; then
  datestr=$(date +"%Y-%m-%d-%H-%M")
  echo "The option datestr(-d) is absent, use new one: $datestr"
fi
if [ -z "$key" ]; then
  key=public.key
  echo "The option key(-k) is absent, use default: $key"
fi

#public_key_file="$target/public-12-27.key"
public_key_file="$target/$key"

inner_zip_file_name="xc-v1.1.4-$datestr.zip"
outer_zip_file_name="bd-$inner_zip_file_name"

base=$(cd $(dirname $0);pwd)
echo base=$base
cd $base

# remove *.zip if exists
if compgen -G "*.zip" > /dev/null; then
  rm *.zip
fi

## 假设bundle文件输出到这个目录
#cd $target
source ~/.crypter/crypter-function.sh
crypter encrypt -pu="$public_key_file" --zip-file="$target/$inner_zip_file_name" --bundle-folder="$target/$datestr"
# clean

mv "$target/$outer_zip_file_name" $base/
cd $base

echo "done. you can download the file from:"
echo "http://192.168.0.25:18080/view/cone-workspaces/job/hxlc-workspace/ws/${outer_zip_file_name}"

set +x