#!/bin/bash

rm -f *.zip

function funzip {
 for zipf in $* 
 do 
 if ! unzip -o $zipf
  then
   return 1
  fi
 done
 return 0
}

name="JOGL-2 nightlies"
wlist="jogl-nightlies-flist.txt"
version="${name} ${wlist}"

echo
echo ${version}
echo
if wget -i ${wlist}
then
 if funzip *.zip 
 then
  cat<<EOF > version.txt
${version}
EOF
  rm *.zip
  exit 0
 else
  echo error
  exit 1
 fi
else
 echo error
 exit 1
fi
