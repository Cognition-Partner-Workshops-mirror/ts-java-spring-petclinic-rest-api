INSERT INTO specialties (id, name, created_at, updated_at) VALUES (1, 'radiology', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO specialties (id, name, created_at, updated_at) VALUES (2, 'surgery', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO specialties (id, name, created_at, updated_at) VALUES (3, 'dentistry', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vets (id, first_name, last_name, created_at, updated_at) VALUES (1, 'James', 'Carter', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO vets (id, first_name, last_name, created_at, updated_at) VALUES (2, 'Helen', 'Leary', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO vets (id, first_name, last_name, created_at, updated_at) VALUES (3, 'Linda', 'Douglas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO vets (id, first_name, last_name, created_at, updated_at) VALUES (4, 'Rafael', 'Ortega', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO vets (id, first_name, last_name, created_at, updated_at) VALUES (5, 'Henry', 'Stevens', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO vets (id, first_name, last_name, created_at, updated_at) VALUES (6, 'Sharon', 'Jenkins', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (2, 1);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 2);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 3);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (4, 2);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (5, 1);

ALTER TABLE specialties ALTER COLUMN id RESTART WITH 100;
ALTER TABLE vets ALTER COLUMN id RESTART WITH 100;
