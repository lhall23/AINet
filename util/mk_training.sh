#! /usr/bin/env bash
# mk_training.sh
# -Lee Hall Mon 29 Oct 2012 02:32:41 AM EDT

# This file should contain already classified data. We'll assume it has 4 tab
# separated columns for the moment.
INPUT_FILE=$1
OUTPUT_TRAIN=temp_train.txt
OUTPUT_DATA=temp_data.txt

#Size of training set as a fraction of the size of the existing data
SIZE=10

[ -z $INPUT_FILE ] && {
    echo "Input file not specified.";
    exit 1
}


len="$(wc -l $INPUT_FILE | cut -f1 -d\  )"
for line in $(seq $(( $len / $SIZE )) ); do
    sed -n "$(( $RANDOM % $len ))p;" $INPUT_FILE
done > $OUTPUT_TRAIN

cat $INPUT_FILE | sed 's/[^ ]*$//' > $OUTPUT_DATA
