-- V2: Seed data for development and testing

INSERT INTO specialty (id, name) VALUES (1, 'radiology');
INSERT INTO specialty (id, name) VALUES (2, 'surgery');
INSERT INTO specialty (id, name) VALUES (3, 'dentistry');

INSERT INTO vet (id, first_name, last_name) VALUES (1, 'James', 'Carter');
INSERT INTO vet (id, first_name, last_name) VALUES (2, 'Helen', 'Leary');
INSERT INTO vet (id, first_name, last_name) VALUES (3, 'Linda', 'Douglas');
INSERT INTO vet (id, first_name, last_name) VALUES (4, 'Rafael', 'Ortega');
INSERT INTO vet (id, first_name, last_name) VALUES (5, 'Henry', 'Stevens');
INSERT INTO vet (id, first_name, last_name) VALUES (6, 'Sharon', 'Jenkins');

INSERT INTO vet_specialty (vet_id, specialty_id) VALUES (2, 1);
INSERT INTO vet_specialty (vet_id, specialty_id) VALUES (3, 2);
INSERT INTO vet_specialty (vet_id, specialty_id) VALUES (3, 3);
INSERT INTO vet_specialty (vet_id, specialty_id) VALUES (4, 2);
INSERT INTO vet_specialty (vet_id, specialty_id) VALUES (5, 1);

-- Reset auto-increment sequences past the seeded IDs
ALTER TABLE specialty ALTER COLUMN id RESTART WITH 100;
ALTER TABLE vet ALTER COLUMN id RESTART WITH 100;
