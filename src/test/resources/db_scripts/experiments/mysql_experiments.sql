-- SELECT a.fk_station_id, TIMESTAMPDIFF(MONTH,b.mintime,a.maxtime) as monthdiff, a.maxtime, b.mintime FROM
SELECT a.fk_station_id, TIMESTAMPDIFF(DAY,b.mintime,a.maxtime) as daydiff, a.maxtime, b.mintime FROM
    (SELECT fk_station_id, MAX(reading_timestamp) AS maxtime FROM concentration_reading GROUP BY fk_station_id) AS a
    JOIN (SELECT fk_station_id, MIN(reading_timestamp) AS mintime FROM concentration_reading GROUP BY fk_station_id) AS b
    ON a.fk_station_id = b.fk_station_id;
--     USING fk_station_id;

SELECT a.fk_station_id, TIMESTAMPDIFF(DAY,b.mintime,a.maxtime) as daydiff, a.maxtime, b.mintime FROM
    (SELECT fk_station_id, MAX(reading_timestamp) AS maxtime FROM concentration_reading GROUP BY fk_station_id) AS a
    NATURAL JOIN (SELECT fk_station_id, MIN(reading_timestamp) AS mintime FROM concentration_reading GROUP BY fk_station_id) AS b;