INSERT INTO monitoring_station (station_external_id, station_name, target_concentration) VALUES ('IC001', 'ice corner 1', 11);
INSERT INTO monitoring_station (station_external_id, station_name, target_concentration) VALUES ('ZO001', 'Zoo Kiosk 2', '22');
INSERT INTO monitoring_station (station_external_id, station_name, target_concentration) VALUES ('MW001', 'Mobile ice wagon 3', '33');


INSERT INTO concentration_reading (fk_station_id, reading_timestamp, actual_concentration) VALUES (1, TIMESTAMP '2021-01-11 11:11:11', 11);
INSERT INTO concentration_reading (fk_station_id, reading_timestamp, actual_concentration) VALUES (1, TIMESTAMP '2021-01-21 21:21:21', 21);
INSERT INTO concentration_reading (fk_station_id, actual_concentration) VALUES (1, 31);
INSERT INTO concentration_reading (fk_station_id, reading_timestamp, actual_concentration) VALUES (2, TIMESTAMP '2021-01-12 12:12:12', 12);
INSERT INTO concentration_reading (fk_station_id, reading_timestamp, actual_concentration) VALUES (2, TIMESTAMP '2021-01-22 22:22:22', 22);
INSERT INTO concentration_reading (fk_station_id, actual_concentration) VALUES (2, 32);
INSERT INTO concentration_reading (fk_station_id, reading_timestamp, actual_concentration) VALUES (3, TIMESTAMP '2021-01-13 13:13:13', 13);
INSERT INTO concentration_reading (fk_station_id, reading_timestamp, actual_concentration) VALUES (3, TIMESTAMP '2021-01-23 23:23:23', 23);
INSERT INTO concentration_reading (fk_station_id, actual_concentration) VALUES (3, 33);