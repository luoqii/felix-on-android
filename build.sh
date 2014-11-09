#!/usr/bin/env bash
#
# bangbang.S

JAR=bundlemgr.jar
jar cfm $JAR sample/bundlemgr/manifest.mf -C sample/bundlemgr/bin/ .
cp -f $JAR assets/felix/preloadbundle
./dexify.sh
