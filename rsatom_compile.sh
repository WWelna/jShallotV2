#!/bin/sh
rm rsatom.o librsatom.so
gcc -c -fpic -DLTM_DESC rsatom.c
gcc -shared -o librsatom.so rsatom.o -ltomcrypt -ltommath
