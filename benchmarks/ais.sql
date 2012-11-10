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

CREATE TABLE source (
	source_id 	SERIAL PRIMARY KEY,
	date		timestamp,
	git_hash	varchar
);

CREATE TABLE benchmark (
	benchmark_id	SERIAL PRIMARY KEY,
	name 			varchar,
	dimensions		INTEGER,
	training_size	INTEGER,
	data_size		INTEGER
);	

INSERT INTO benchmark(name,dimensions,training_size,data_size) VALUES 
	('ground_training1', 3, 2384, 23485), 
	('ground_training2', 3, 3649, 36490),
	('iris', 4, 13, 149),
	('wine', 16, 17, 178);

CREATE TABLE test (
	test_id			SERIAL PRIMARY KEY,
	source_id		INTEGER REFERENCES source(source_id),
	benchmark_id	INTEGER REFERENCES benchmark(benchmark_id),
	time			INTERVAL,
	correct			INTEGER
);	

COMMIT;

