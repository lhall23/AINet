#! /usr/bin/env bash
# get_report.sh
# -Lee Hall Sun 11 Nov 2012 01:11:40 PM EST

if [ ! -z "$1" ]; then
    COMMIT="$( git rev-parse $1 )"; 
    SOURCE_ID="$(echo "SELECT source_id 
            FROM source WHERE git_hash='$COMMIT';" |
        psql -t -d ais )" 
    [ -z "$SOURCE_ID" ] && {
        echo "No benchmarks have been run on this revision";
        exit 1;
    }
    CONSTRAINT="WHERE source_id=$SOURCE_ID";
    git log --oneline "$COMMIT" | head -1
fi
echo '
SELECT name AS "Name" ,iterations AS "Iterations",
        ROUND(compression::NUMERIC, 2) AS "Compression", 
        -- DATE_TRUNC('seconds', AVG(runtime)) AS avg_runtime, 
        -- DATE_TRUNC('seconds', STDDEV_POP(runtime)) AS std_dev, 
        ROUND(AVG(PERCENT(wrong,data_size))::NUMERIC * 100,2) 
            AS "Percent Correct", 
        ROUND(STDDEV_POP(PERCENT(wrong,data_size))::NUMERIC * 100,2) 
            AS "Standard Dev",
        COUNT(*) AS "Trials"
    FROM test 
    JOIN benchmark USING (benchmark_id)'"
    $CONSTRAINT
    GROUP BY iterations,compression,name
    ORDER BY name,iterations DESC, compression;
" | psql -d ais
