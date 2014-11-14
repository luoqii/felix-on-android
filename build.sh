#!/usr/bin/env bash
#
# bangbang.S

JAR=bundlemgr.jar
jar cfm $JAR sample/bundlemgr/manifest.mf -C sample/bundlemgr/bin/ .
cp -f sample/bundlemgr/resapk/* res.apk
jar uf $JAR -C . res.apk
cp -f $JAR assets/felix/preloadbundle
./dexify.sh
