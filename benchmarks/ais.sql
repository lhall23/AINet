/*
 * ais.sql 
 * -Lee Hall Fri 09 Nov 2012 06:59:05 PM EST
 */

/*  
 * 	If we need to drop a table, we can use a conditional drop like this:
 *  DROP TABLE IF EXISTS table_name;	
 *  And, better yet, we can put it in the transaction, so it rolls back 
 *	if things go belly up.
 */
BEGIN;


DROP TABLE IF EXISTS source CASCADE;
CREATE TABLE source (
	source_id 	SERIAL PRIMARY KEY,
	date		timestamp,
	git_hash	varchar UNIQUE
);

DROP TABLE IF EXISTS benchmark CASCADE;
CREATE TABLE benchmark (
	benchmark_id	SERIAL PRIMARY KEY,
	name 			varchar UNIQUE,
	dimensions		INTEGER,
	training_size	INTEGER,
	data_size		INTEGER
);	

INSERT INTO benchmark(name,dimensions,training_size,data_size) VALUES 
	('ground_training1', 3, 2384, 23485), 
	('ground_training2', 3, 3649, 36490),
	('iris', 4, 13, 149),
	('wine', 16, 17, 178);

DROP TABLE IF EXISTS test CASCADE;
CREATE TABLE test (
	test_id			SERIAL PRIMARY KEY,
	source_id		INTEGER REFERENCES source(source_id),
	benchmark_id	INTEGER REFERENCES benchmark(benchmark_id),
	date			timestamp DEFAULT NOW(),
	runtime			INTERVAL, 	--Runtime
	scale			INTEGER, 	--Size of Antibody population
	iterations		INTEGER, 	--number of iterations to breed Antibodies
	wrong			INTEGER 	--Incorrect classifications
);	


CREATE AGGREGATE STDDEV_POP(INTERVAL) (
    SFUNC=float8_accum,
    STYPE=FLOAT[],
    INITCOND='{0,0,0}',
    FINALFUNC=interval_stddev_pop
);

--Taking a std_dev of an interval would also be useful

--Cast the interval into the accumulator
CREATE OR REPLACE FUNCTION float8_accum(FLOAT[],INTERVAL) 
	RETURNS FLOAT[] AS $$
BEGIN
    RETURN float8_accum($1, EXTRACT('epoch' FROM $2));
END;
$$ LANGUAGE plpgsql;

--Cast the return value back to an interval
CREATE OR REPLACE FUNCTION interval_stddev_pop(FLOAT[]) 
	RETURNS INTERVAL AS $$
BEGIN
    RETURN (float8_stddev_pop($1) || ' s')::interval;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION interval_stddev_samp(FLOAT[]) 
	RETURNS INTERVAL AS $$
BEGIN
    RETURN (float8_stddev_samp($1) || ' s')::interval;
END;
$$ LANGUAGE plpgsql;

--Define the actuall aggregate
CREATE AGGREGATE STDDEV_POP(INTERVAL) (
    SFUNC=float8_accum,
    STYPE=FLOAT[],
    INITCOND='{0,0,0}',
    FINALFUNC=interval_stddev_pop
);

--Define the actuall aggregate
CREATE AGGREGATE STDDEV_SAMP(INTERVAL) (
    SFUNC=float8_accum,
    STYPE=FLOAT[],
    INITCOND='{0,0,0}',
    FINALFUNC=interval_stddev_samp
);



--This makes a lot of stuff prettier later.
CREATE OR REPLACE FUNCTION PERCENT(INTEGER, INTEGER) RETURNS FLOAT AS $$
BEGIN
    RETURN ($2 - $1)::FLOAT/$2;
END;
$$ LANGUAGE plpgsql;

COMMIT;

