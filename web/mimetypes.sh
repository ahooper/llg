#!/bin/bash

function mimetype {
 T=$1
 shift
 F=$*
 if [ -n "${T}" ]&&[ -n "${F}" ]
 then
  svn ps svn:mime-type ${T} ${F}
 fi
}

mimetype application/x-java-archive $(find . -type f -name "*.jar")
mimetype application/x-java-jnlp-file $(find . -type f -name "*.jnlp")
mimetype text/plain $(find . -type f -name "*.txt" -o -name "*.sh")
mimetype application/x-3ds $(find . -type f -name "*.3ds")
mimetype image/png $(find . -type f -name "*.png")
mimetype image/jpeg $(find . -type f -name "*.jpg" -o -name "*.jpeg")
