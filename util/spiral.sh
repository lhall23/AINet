#! /usr/bin/env bash
# spiral.sh
# -Lee Hall Thu 06 Dec 2012 03:43:09 AM EST

for a in $(seq $1); do
    m=$RANDOM;
    c=$(echo "$a%2 + 1" | bc )
    if [ $c -eq 1 ]; then  
        sign="-1 * ";
    else 
        sign=""
    fi
    x=$(echo "$sign $m/256 * c($m/256) + 128" | bc -l)
    y=$(echo "$sign $m/256 * s($m/256) + 128" | bc -l)
    echo "$x,$y,$c"
done
