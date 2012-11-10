#! /usr/bin/env bash
# benchmarks.sh
# -Lee Hall Thu 08 Nov 2012 11:08:18 PM EST

BENCH_DIR="benchmarks"
#This just gets head, it won't save the current one if we're benchmarking older commits
CURRENT="$(git log |head -3)"
OUTPUT="$(mktemp)"
SCALE=500
ITERATIONS=500
TRIALS=10

# Run the current version of AINet against each of these tests $TRIALS times 
# Record individual results in each test directory, and a summary of all the
# trials of each test in  
make AINet
echo -en "${CURRENT}\nScale: ${SCALE}\tIterations: ${ITERATIONS}" | 
    tee -a "${BENCH_DIR}/results.txt"
for TEST in "$BENCH_DIR"/*; do
    DIMENSIONS="$( sed -n '1s/[^\t ,]*[\t ,]\+/@/gp' "${TEST}/classified.txt" | 
        tr -d -c @ | wc -c)";
    TOTAL="$( wc -l "${TEST}/classified.txt" | cut -d \  -f1 )"
    SUM=0;
    echo -en "${CURRENT}\nScale: ${SCALE}\tIterations: ${ITERATIONS}" | 
        tee -a "${TEST}/test_results.txt"
    for num in $( seq $TRIALS ); do 
        java AINet -t "${TEST}/train.txt" -f "${TEST}/test_data.txt" \
            -o "${OUTPUT}"  -d "${DIMENSIONS}" -s "${SCALE}" -i "${ITERATIONS}" 
        INCORRECT="$( ./util/compare.sh "$OUTPUT" "${TEST}/classified.txt" | 
            wc -l )"
        CORRECT="$(( $TOTAL - $INCORRECT ))"
        PERCENT="$(echo "scale=2;$CORRECT / $TOTAL" | bc -l )"
        { 
            echo -en "Results: ${CORRECT}/${TOTAL} (${PERCENT}%)\n"
        } | tee -a "${TEST}/test_results.txt"
        SUM=$(( $SUM + $CORRECT ))
        COR_ARRAY[$num]=$CORRECT;
    done
    AVG=$( echo "$SUM / $TRIAL" | bc -l )
    SUM=0;
    for num in $(seq $TRIALS); do 
        SUM=$( echo "$SUM + ($AVG - ${COR_ARRAY[$num]})^2" | bc -l )
    done
    VAR=$( echo "scale=2; $SUM / $TRIALS" | bc -l )
    PERC=$( echo "scale=2; $AVG / $TOTAL" | bc -l )
    echo -e "$( basename $TEST ): Avg: $AVG/$TOTAL (${PERC}%)Var: $VAR\n\n" | 
        tee -a "${BENCH_DIR}/results.txt"
done
rm "${OUTPUT}"
