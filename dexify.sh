#!/usr/bin/env bash
#
# bangbang.S
# inspired by http://nilvec.com/embedding-osgi-into-an-android-application-part-1.html

dexify() {
    for f in $*; do
        tmpdir="`mktemp -d`"
        tmpfile="${tmpdir}/classes.dex"
        dx --dex --output=${tmpfile} ${f}
        aapt add ${f} ${tmpfile}
        rm -f ${tmpfile}
        rmdir ${tmpdir}
    done
}
set -x
dexify assets/felix/preloadbundle/*
