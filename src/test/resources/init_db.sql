CREATE DATABASE IF NOT EXISTS martinfowler_uiArchs CHARACTER SET UTF8 COLLATE UTF8_BIN;
USE martinfowler_uiArchs;

CREATE TABLE IF NOT EXISTS monitoring_station (
	id SERIAl PRIMARY KEY,
	station_external_id VARCHAR(256) NOT NULL,
	INDEX monitoring_station__idx__station_external_id (station_external_id ASC),
	INDEX monitoring_station__combined_idx (
		id ASC,
		station_external_id ASC
	),
	station_name VARCHAR(4092) DEFAULT NULL,
	target_concentration SMALLINT NOT NULL
)
	-- AUTO_INCREMENT = id
;


CREATE TABLE IF NOT EXISTS concentration_reading (
	id SERIAl PRIMARY KEY,
	fk_station_id BIGINT UNSIGNED NOT NULL,
	INDEX concentration_reading__idx__fk_station_id (fk_station_id ASC),
	FOREIGN KEY (fk_station_id)
		REFERENCES monitoring_station(id)
		ON DELETE SET DEFAULT
		ON UPDATE CASCADE,
	reading_timestamp TIMESTAMP NOT NULL,
	INDEX concentration_reading__idx__reading_timestamp (reading_timestamp ASC),
	actual_concentration SMALLINT NOT NULL
)
	-- AUTO_INCREMENT = id
;
