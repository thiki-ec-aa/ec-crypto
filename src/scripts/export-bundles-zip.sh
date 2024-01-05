#!/bin/bash
set -x

public_key_file=public-12-27.key
datestr=$(date +"%Y-%m-%d-%H-%M")
file_prefix=bd-xc-v1.1.4
root=/root/hxlc-workspace

base=$(cd $(dirname $0);pwd)
echo base=$base
cd $base
rm *.zip

## 假设bundle文件输出到这个目录
cd $root/bundles/
source ~/.crypter/crypter-function.sh
crypter encrypt -pu=$public_key_file --zip-file=$file_prefix-$datestr.zip --bundle-folder=$datestr
# clean

mv $file_prefix-$datestr.zip $base/
cd $base

echo "done. you can download the file from:"
echo "http://192.168.0.25:18080/view/cone-workspaces/job/hxlc-workspace/ws/$file_prefix-${datestr}.zip"

set +x