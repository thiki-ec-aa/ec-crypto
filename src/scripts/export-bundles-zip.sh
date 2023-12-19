#!/bin/bash

base=$(cd $(dirname $0);pwd)
echo base=$base
cd $base
rm *.zip

root=/root/hxlc-workspace

cd $root

if [ -z "$1" ]; then
  echo "Input argument is missing. use default set-env.sh"
  list="$root/set-env.sh"
else
  list=$1
fi

source $list

datestr=$(date +"%Y-%m-%d-%H-%M")
mkdir -p bundles/$datestr

for ((i=0; i<${#components[*]} / 2; i++))
do
  branch=${components[i*2+1]}
  dir=${components[i*2]}
  IFS="/" read -ra parts <<< "$dir"
  project_name="${parts[${#parts[@]}-1]}"
  echo branch="$branch"
  echo dir="$dir"
  echo project_name="$project_name"

  cd $root/$dir
  suffix=${branch//\//-}
  git checkout ${branch}
  git pull

  git bundle create "$root/bundles/$datestr/$project_name.$suffix.bundle" "$branch"

done

# exclusions
cd $root/bundles/$datestr
rm cone-dolphinschedule* xc-osp.osp-vone* -f

cd $root/bundles/
java -jar crypter-boot.jar --public-key-file=public.key --zip-file-name=xc-v1.1.4-$datestr.zip --bundle-folder-name=$datestr encrypt
# clean
rm xc-v1.1.4-$datestr.zip
rm $datastr -rf
rm token.txt

mv bd-xc-v1.1.4-$datestr.zip $base/
cd $base

echo "done. you can download the file from:"
echo "http://192.168.0.25:18080/view/cone-workspaces/job/hxlc-workspace/ws/bd-xc-v1.1.4-${datestr}.zip"

echo "Press any key to continue"
read -n 1

