#!/usr/bin/env bash
#
# bangbang.S
# inspired by http://nilvec.com/embedding-osgi-into-an-android-application-part-1.html

dexify() {
    for f in $*; do
        tmpfile="classes.dex"
        dx --dex --output=${tmpfile} ${f}
        aapt add ${f} ${tmpfile}
        rm -f ${tmpfile}d
    done
}

dexify assets/felix/preloadbundle/*
