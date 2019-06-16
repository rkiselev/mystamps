--
-- Auto-generated by Maven, based on values from src/main/resources/test/spring/test-data.properties
--

-- Used below as series' owner
INSERT INTO users(id, login, role, name, registered_at, activated_at, hash, salt, email) VALUES
	(4, 'test1', 'USER', 'Series Owner', NOW(), NOW(), '@old_valid_user_password_hash@', '@old_valid_user_password_salt@', 'test1@example.org');

-- Used only in src/test/robotframework/series/creation/misc-user.robot
INSERT INTO images(type) VALUES('PNG');
INSERT INTO series(id, quantity, perforated, image_url, created_at, created_by, updated_at, updated_by) VALUES
	(1, 1, TRUE, '/image/1', NOW(), 4, NOW(), 4);

INSERT INTO michel_catalog(code) VALUES('99');
INSERT INTO series_michel_catalog(series_id, michel_id) SELECT 1, id FROM michel_catalog WHERE code = '99';

INSERT INTO scott_catalog(code) VALUES('99');
INSERT INTO series_scott_catalog(series_id, scott_id) SELECT 1, id FROM scott_catalog WHERE code = '99';

INSERT INTO yvert_catalog(code) VALUES('99');
INSERT INTO series_yvert_catalog(series_id, yvert_id) SELECT 1, id FROM yvert_catalog WHERE code = '99';

INSERT INTO gibbons_catalog(code) VALUES('99');
INSERT INTO series_gibbons_catalog(series_id, gibbons_id) SELECT 1, id FROM gibbons_catalog WHERE code = '99';
