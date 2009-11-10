#!/bin/bash

if [ -d web ]&&[ -d trunk ]
then
 oldv=$(ls web/llg*.jar | sed 's%web/%%')
 newv=$(ls trunk/llg*.jar | sed 's%trunk/%%')
 if [ -n "${oldv}" ]&&[ -n "${newv}" ]&&[ "${oldv}" != "${newv}" ]
 then
  for src in $(egrep "${oldv}" $(find . -type f -name '*.html' -o -name '*.xml') | sed 's/:.*//' | sort -u )
  do
   if cat ${src} | sed "s%${oldv}%${newv}%g" > /tmp/tmp
   then
    cp /tmp/tmp ${src}
    ls -l ${src}
   else
    echo "Error rewriting, no changes performed."
    exit 1
   fi
  done
  if svn delete --force web/${oldv}
  then
   if cp -p trunk/${newv} web/${newv}
   then
    if svn add web/${newv}
    then
     ls -l web/${newv}
    else
     echo "Error from svn add."
     exit 1
    fi
   else
    echo "Error from file copy."
    exit 1
   fi
  else
   echo "Error from svn delete."
   exit 1
  fi
 else
  echo "Ok, no changes found."
 fi
 exit 0
else
 echo "Error, directories not found."
 exit 1
fi
