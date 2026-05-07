INSERT INTO rental_points (rental_point_id, location, rental_point_type, parent_point_id, deleted)
VALUES (1, 'г. Москва', 'CITY', NULL, FALSE);

INSERT INTO rental_points (rental_point_id, location, rental_point_type, parent_point_id, deleted)
VALUES (2, 'Марьино', 'DISTRICT', 1, FALSE);

INSERT INTO rental_points (rental_point_id, location, rental_point_type, parent_point_id, deleted)
VALUES (3, 'ул. Перерва', 'STREET', 2, FALSE);

INSERT INTO rental_points (rental_point_id, location, rental_point_type, parent_point_id, deleted)
VALUES (4, '43', 'BUILDING', 3, FALSE);

INSERT INTO rental_points (rental_point_id, location, rental_point_type, parent_point_id, deleted)
VALUES (5, '74', 'BUILDING', 3, FALSE);

INSERT INTO rental_points (rental_point_id, location, rental_point_type, parent_point_id, deleted)
VALUES (6, 'ул. Люблинская', 'STREET', 2, FALSE);

INSERT INTO rental_points (rental_point_id, location, rental_point_type, parent_point_id, deleted)
VALUES (7, '84', 'BUILDING', 6, FALSE);

INSERT INTO rental_points (rental_point_id, location, rental_point_type, parent_point_id, deleted)
VALUES (8, 'Тропарево-Никулино', 'DISTRICT', 1, FALSE);

INSERT INTO rental_points (rental_point_id, location, rental_point_type, parent_point_id, deleted)
VALUES (9, 'ул. Покрышкина', 'STREET', 8, FALSE);

INSERT INTO rental_points (rental_point_id, location, rental_point_type, parent_point_id, deleted)
VALUES (10, '24', 'BUILDING', 9, FALSE);


INSERT INTO scooters (scooter_id, model, battery_level, scooter_status, rental_point_id, deleted, mileage)
VALUES (1, 'Xiaomi 12', 100, 'AVAILABLE', 10, FALSE, 13);

INSERT INTO scooters (scooter_id, model, battery_level, scooter_status, rental_point_id, deleted, mileage)
VALUES (2, 'Xiaomi 13', 76, 'AVAILABLE', 7, FALSE, 24);

INSERT INTO scooters (scooter_id, model, battery_level, scooter_status, rental_point_id, deleted, mileage)
VALUES (3, 'Xiaomi 12', 4, 'IN_SERVICE', 4, FALSE, 76);

INSERT INTO scooters (scooter_id, model, battery_level, scooter_status, rental_point_id, deleted, mileage)
VALUES (4, 'Xiaomi 12', 100, 'IN_WAREHOUSE', NULL, FALSE, 52);


INSERT INTO tariffs (tariff_id, payment_type, price, discount)
VALUES (1, 'PER_MINUTE', 7, 15);

INSERT INTO tariffs (tariff_id, payment_type, price, discount)
VALUES (2, 'PER_MINUTE', 5, 0);

INSERT INTO tariffs (tariff_id, payment_type, price, discount)
VALUES (3, 'SEASON_TICKET', 200, 0);

SELECT setval(pg_get_serial_sequence('rental_points', 'rental_point_id'), coalesce(max(rental_point_id), 1), max(rental_point_id) IS NOT NULL) FROM rental_points;
SELECT setval(pg_get_serial_sequence('scooters', 'scooter_id'), coalesce(max(scooter_id), 1), max(scooter_id) IS NOT NULL) FROM scooters;
SELECT setval(pg_get_serial_sequence('tariffs', 'tariff_id'), coalesce(max(tariff_id), 1), max(tariff_id) IS NOT null) FROM tariffs;