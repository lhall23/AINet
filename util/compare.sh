#! /usr/bin/env bash
# util/compare.sh
# -Lee Hall Thu 08 Nov 2012 03:39:58 PM EST

INPUT1=$1
INPUT2=$2

TEMP_FILE=$(mktemp);
sed -n 's/^.*[, \t]\+//p' $INPUT1 | cat -n  > $TEMP_FILE
sed -n 's/^.*[, \t]\+//p' $INPUT2 | cat -n | 
    diff  -y -B -w --suppress-common-lines - $TEMP_FILE
rm $TEMP_FILE

