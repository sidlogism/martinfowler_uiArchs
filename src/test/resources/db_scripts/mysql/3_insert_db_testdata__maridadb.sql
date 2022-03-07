USE martinfowler_uiArchs;

INSERT INTO monitoring_station (id, station_external_id, station_name, target_concentration) VALUES (NULL, "IC001", "ice corner 1", 11);
INSERT INTO monitoring_station (id, station_external_id, station_name, target_concentration) VALUES (NULL, "ZO001", "Zoo Kiosk 2", 22);
INSERT INTO monitoring_station (id, station_external_id, station_name, target_concentration) VALUES (NULL, "MW001", "Mobile ice wagon 3", 33);


INSERT INTO concentration_reading (id, fk_station_id, reading_timestamp, actual_concentration) VALUES (NULL, 1, "2021-01-11 11:11:11", 11);
INSERT INTO concentration_reading (id, fk_station_id, reading_timestamp, actual_concentration) VALUES (NULL, 1, "2021-01-21 21:21:21", 21);
INSERT INTO concentration_reading (id, fk_station_id, reading_timestamp, actual_concentration) VALUES (NULL, 1, NULL, 31);
INSERT INTO concentration_reading (id, fk_station_id, reading_timestamp, actual_concentration) VALUES (NULL, 2, "2021-01-12 12:12:12", 12);
INSERT INTO concentration_reading (id, fk_station_id, reading_timestamp, actual_concentration) VALUES (NULL, 2, "2021-01-22 22:22:22", 22);
INSERT INTO concentration_reading (id, fk_station_id, reading_timestamp, actual_concentration) VALUES (NULL, 2, NULL, 32);
INSERT INTO concentration_reading (id, fk_station_id, reading_timestamp, actual_concentration) VALUES (NULL, 3, "2021-01-13 13:13:13", 13);
INSERT INTO concentration_reading (id, fk_station_id, reading_timestamp, actual_concentration) VALUES (NULL, 3, "2021-01-23 23:23:23", 23);
INSERT INTO concentration_reading (id, fk_station_id, reading_timestamp, actual_concentration) VALUES (NULL, 3, NULL, 33);
