select * from monitoring_station;
select * from concentration_reading;
SELECT id, fk_station_id, TO_CHAR(reading_timestamp, 'yyyy-mm-dd hh24:mi:ss') as reading_timestamp, actual_concentration
FROM concentration_reading;

SELECT id, fk_station_id, reading_timestamp, actual_concentration
FROM concentration_reading
WHERE fk_station_id = 1 AND reading_timestamp =
(
	SELECT MAX(reading_timestamp)
	FROM concentration_reading
	WHERE fk_station_id = 1
);

SELECT id, fk_station_id, reading_timestamp, actual_concentration
FROM concentration_reading
WHERE reading_timestamp =
(
	SELECT MAX(cr.reading_timestamp)
	FROM concentration_reading cr JOIN monitoring_station ms on (cr.fk_station_id = ms.id)
	WHERE ms.id = 1
);
