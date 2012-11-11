#! /usr/bin/env bash
# get_report.sh
# -Lee Hall Sun 11 Nov 2012 01:11:40 PM EST

echo "
SELECT name,scale,iterations, 
        DATE_TRUNC('seconds', AVG(runtime)) AS avg_runtime, 
        DATE_TRUNC('seconds', STDDEV_POP(runtime)) AS std_dev, 
        AVG(PERCENT(wrong,data_size)) AS percent_correct, 
        STDDEV_POP(PERCENT(wrong,data_size)) AS percent_stddev  
    FROM test 
    JOIN benchmark USING (benchmark_id) 
    GROUP BY iterations,scale,name
    ORDER BY name,iterations DESC, scale;
" | psql -d ais
