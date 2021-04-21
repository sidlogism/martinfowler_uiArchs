CREATE SEQUENCE monitoring_station__sqn__id INCREMENT BY 1;
CREATE TABLE monitoring_station (
	id NUMBER DEFAULT monitoring_station__sqn__id.nextval NOT NULL PRIMARY KEY, -- PRIMARY KEY contraint implies UNIQUE
	station_external_id VARCHAR2(255) NOT NULL UNIQUE, -- UNIQUE implies implicit index creatien
	station_name VARCHAR2(4000) DEFAULT NULL,
	target_concentration SMALLINT NOT NULL
);
CREATE INDEX monitoring_station__combined_idx ON monitoring_station (
		id ASC,
		station_external_id ASC
);


CREATE SEQUENCE concentration_reading__sqn__id INCREMENT BY 1;
CREATE TABLE concentration_reading (
	id NUMBER DEFAULT concentration_reading__sqn__id.nextval NOT NULL PRIMARY KEY, -- PRIMARY KEY contraint implies UNIQUE
	fk_station_id NUMBER NOT NULL,
	FOREIGN KEY (fk_station_id)
		REFERENCES monitoring_station(id)
        ON DELETE SET NULL, -- ON UPDATE CASCADE missing in Oracle SQL. Maybe already implicit behaviour?
	reading_timestamp DATE DEFAULT SYSDATE NOT NULL,
	actual_concentration NUMBER(5,0) NOT NULL
);


CREATE INDEX concentration_reading__idx__fk_station_id ON concentration_reading (fk_station_id ASC);
CREATE INDEX concentration_reading__idx__reading_timestamp ON concentration_reading (reading_timestamp ASC);