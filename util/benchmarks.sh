#! /usr/bin/env bash
# benchmarks.sh
# -Lee Hall Thu 08 Nov 2012 11:08:18 PM EST

BENCH_DIR="benchmarks"
#This just gets head, it won't save the current one if we're benchmarking older commits
CURRENT="$(git log -n1 --format=short HEAD)"
GIT_HASH="$(git log -n1 --format=%H HEAD)"
GIT_DATE="$(git log -n1 --format=%aD HEAD)"

OUTPUT="$(mktemp)"
SCALE=500
ITERATIONS=500
TRIALS=5

#Check for uncommitted changes
[ -z "$(git status -s)" ] || {
    echo "Uncommitted changes."
    echo "Are we logging this with the correct revision? [y/N]"
    read prompt
    [ "x${prompt}x" == "xyx" ] ||
        exit 1
}


# Run the current version of AINet against each of these tests $TRIALS times 
# Record individual results in each test directory, and a summary of all the
# trials of each test in  
make AINet

#SELECT OR INSERT record for current software revision.
SOURCE_ID="$(
    echo "SELECT source_id FROM source WHERE git_hash='$GIT_HASH'" |
        psql -t -d ais | tr -d [:space:])"
[ -z "$SOURCE_ID" ] && {
    SOURCE_ID="$(
        echo "INSERT INTO source(date,git_hash) 
                VALUES ('$GIT_DATE','$GIT_HASH') RETURNING source_id;" | 
            psql -q -t -d ais | tr -d [:space:])";
}
    

#Setup file logging.
echo -en "${CURRENT}\nScale: ${SCALE}\tIterations: ${ITERATIONS}\n" | 
    tee -a "${BENCH_DIR}/results.txt"

for TEST in "$BENCH_DIR"/*; do
    [ -d "$TEST" ] ||
        continue
    DIMENSIONS="$( sed -n '1s/[^\t ,]*[\t ,]\+/@/gp' "${TEST}/classified.txt" | 
        tr -d -c @ | wc -c)";
    TOTAL="$( wc -l "${TEST}/classified.txt" | cut -d \  -f1 )"
    TEST_NAME="$(basename $TEST)"
    BENCHMARK_ID="$(
        echo "SELECT benchmark_id FROM benchmark WHERE name='$TEST_NAME'" |
            psql -t -d ais | tr -d [:space:])"
    echo -en "${CURRENT}\nScale: ${SCALE}\tIterations: ${ITERATIONS}\n" | 
        tee -a "${TEST}/test_results.txt"

    SUM=0;
    for num in $( seq $TRIALS ); do 
        start_time="$(date)"
        java AINet -t "${TEST}/train.txt" -f "${TEST}/test_data.txt" \
            -o "${OUTPUT}"  -d "${DIMENSIONS}" -s "${SCALE}" \
            -i "${ITERATIONS}" 
        end_time="$(date)"
        INCORRECT="$( ./util/compare.sh "$OUTPUT" "${TEST}/classified.txt" | 
            wc -l )"
        echo "INSERT INTO test(source_id,benchmark_id,runtime,scale,
                iterations,wrong) VALUES
            ($SOURCE_ID,$BENCHMARK_ID,
                '$end_time'::timestamp - '$start_time'::timestamp,$SCALE,
                $ITERATIONS,$INCORRECT);" | psql -q -d ais
        CORRECT="$(( $TOTAL - $INCORRECT ))"
        PERCENT="$(echo "scale=2;$CORRECT / $TOTAL" | bc -l )"
        { 
            echo -en "Results: ${CORRECT}/${TOTAL} (${PERCENT}%)\n"
        } | tee -a "${TEST}/test_results.txt"
        SUM=$(( $SUM + $CORRECT ))
        COR_ARRAY[$num]=$CORRECT;
    done
    AVG=$(  echo "scale=0; $SUM / $TRIALS" | bc -l )
    SUM=0;
    for num in $(seq $TRIALS); do 
        SUM=$( echo "$SUM + ($AVG - ${COR_ARRAY[$num]})^2" | bc -l )
    done
    VAR=$( echo "scale=2; $SUM / $TRIALS" | bc -l )
    PERC=$( echo "scale=2; $AVG / $TOTAL" | bc -l )
    echo -e "$TEST_NAME:\n\t Avg: $AVG/$TOTAL (${PERC}%) Var: $VAR\n\n" | 
        tee -a "${BENCH_DIR}/results.txt"
done
rm "${OUTPUT}"
