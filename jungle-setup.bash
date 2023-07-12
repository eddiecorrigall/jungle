#!/bin/bash

export DIR=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")

alias jungle="java -Dfile.encoding=UTF-8 -jar "$DIR/out/artifacts/jungle_jar/jungle.jar""
