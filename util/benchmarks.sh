#! /usr/bin/env bash
# benchmarks.sh
# -Lee Hall Thu 08 Nov 2012 11:08:18 PM EST

BENCH_DIR="benchmarks"
#This just gets head, it won't save the current one if we're benchmarking older commits
CURRENT="$(git log -n1 --format=short HEAD)"
GIT_HASH="$(git log -n1 --format=%H HEAD)"
GIT_DATE="$(git log -n1 --format=%aD HEAD)"

OUTPUT="$(mktemp)"
COMPRESSION=".2"
ITERATIONS=50
TRIALS=5

SAVE=true

function usage(){
    echo -en "Usage:\n"
    echo -en "\t-t:\tTest mode (Don't save output, enable debugging)\n"
    echo -en "\t-d:\tEnable debugging\n"
    echo -en "\t-c COMPRESSION:\tCompression rate\n"
    echo -en "\t-T TRIALS:\tNumber of trials\n"
    echo -en "\t-i ITERATIONS:\tNumber of iterations\n"
    echo -en "\t-h:\tPrint this message\n"
}

# Get options 
while getopts "tdhc:T:i:" OPT; do
    case $OPT in
        t)
            echo "Test run. Reporting disabled, debugging enabled."
            SAVE=false;
            TRIALS=1;
            ;;
        d)
            echo "Debugging enabled."
            DEBUG="-D";
            ;;
        i)
            ITERATIONS="$OPTARG";
            echo "Iterations set to $ITERATIONS."
            ;;
        h)
            usage;
            exit 0;
            ;;
        c)
            COMPRESSION=$OPTARG
            ;;
        T)
            TRIALS=$OPTARG
            ;;
        *)
            echo "Option not recognized"
            usage;
            exit 1;
            ;;
    esac
done
echo $@
echo $(( $OPTIND - 1 ))
shift $(( $OPTIND - 1 ))
echo $@


# If there are arguments supplied on the commandline, they are tests
if [ -z "$@" ]; then 
    for TEST in benchmarks/*; do
        [ -d "$TEST" ] &&
            TEST_LIST+=" $TEST";
    done
else 
    for TEST in $@; do 
        TEST="$BENCH_DIR/$TEST";
        if [ -d "$TEST" ]; then
            TEST_LIST+=" $TEST" 
        else
            echo "Cannot find directory for $TEST."
        fi
    done
fi
[ -z "$TEST_LIST" ] &&
    echo "No tests selected." &&
    exit 1 
echo "Selected tests: $TEST_LIST"

#Check for uncommitted changes
[ "x${SAVE}x" == "xtruex" ] && [ -n "$(git status -s)" ] && {
    echo "Uncommitted changes."
    echo "Are we logging this with the correct revision? [y/N]"
    read prompt
    [ "x${prompt}x" == "xyx" ] ||
        exit 1
}


#Make sure we're using the current version
JAR_TMP=$(mktemp)
make AINet.jar
mv AINet.jar "$JAR_TMP"

#SELECT OR INSERT record for current software revision.
[ "x${SAVE}x" == "xtruex" ] && {
    SOURCE_ID="$(
        echo "SELECT source_id FROM source WHERE git_hash='$GIT_HASH'" |
            psql -t -d ais | tr -d [:space:])"
    [ -z "$SOURCE_ID" ] && {
        SOURCE_ID="$(
            echo "INSERT INTO source(date,git_hash) 
                    VALUES ('$GIT_DATE','$GIT_HASH') RETURNING source_id;" | 
                psql -q -t -d ais | tr -d [:space:])";
    }
}
    

#Setup file logging.
echo -en "${CURRENT}\nCompression: ${COMPRESSION}\tIterations: ${ITERATIONS}\n" | 
    tee -a "${BENCH_DIR}/results.txt"

for TEST in $TEST_LIST; do
    [ -d "$TEST" ] ||
        continue
    DIMENSIONS="$( sed -n '1s/[^\t ,]*[\t ,]\+/@/gp' "${TEST}/classified.txt" | 
        tr -d -c @ | wc -c)";
    TOTAL="$( wc -l "${TEST}/classified.txt" | cut -d \  -f1 )"
    TEST_NAME="$(basename $TEST | sed 's/[0-9]\+-//')"
    BENCHMARK_ID="$(
        echo "SELECT benchmark_id FROM benchmark WHERE name='$TEST_NAME'" |
            psql -t -d ais | tr -d [:space:])"
    echo -en "${CURRENT}\nCompression: ${COMPRESSION}\tIterations: ${ITERATIONS}\n" | 
        tee -a "${TEST}/test_results.txt"

    SUM=0;
    for num in $( seq $TRIALS ); do 
        start_time="$(date)"
        java -jar -ea "$JAR_TMP" -t "${TEST}/train.txt" \
            -f "${TEST}/test_data.txt" -o "${OUTPUT}"  -d "${DIMENSIONS}" \
            -z "${COMPRESSION}" -i "${ITERATIONS}" "${DEBUG}" || exit 1
        end_time="$(date)"
        INCORRECT="$( ./util/compare.sh "$OUTPUT" "${TEST}/classified.txt" | 
            wc -l )"
        [ "x${SAVE}x" == "xtruex" ] && {
            echo "INSERT INTO test(source_id,benchmark_id,runtime,compression,
                iterations,wrong) VALUES
            ($SOURCE_ID,$BENCHMARK_ID,
                '$end_time'::timestamp - '$start_time'::timestamp,$COMPRESSION,
                $ITERATIONS,$INCORRECT);" | psql -q -d ais
        }
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
