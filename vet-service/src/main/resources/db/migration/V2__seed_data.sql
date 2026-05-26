-- ===========================================================================
-- V2: Seed data for the dev profile
-- Populates sample specialties and vets matching the original PetClinic data.
-- ===========================================================================

-- Insert specialties
INSERT INTO specialties (name) VALUES ('radiology');
INSERT INTO specialties (name) VALUES ('surgery');
INSERT INTO specialties (name) VALUES ('dentistry');

-- Insert vets
INSERT INTO vets (first_name, last_name) VALUES ('James', 'Carter');
INSERT INTO vets (first_name, last_name) VALUES ('Helen', 'Leary');
INSERT INTO vets (first_name, last_name) VALUES ('Linda', 'Douglas');
INSERT INTO vets (first_name, last_name) VALUES ('Rafael', 'Ortega');
INSERT INTO vets (first_name, last_name) VALUES ('Henry', 'Stevens');
INSERT INTO vets (first_name, last_name) VALUES ('Sharon', 'Jenkins');

-- Assign specialties to vets
-- Helen Leary -> radiology
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (2, 1);
-- Linda Douglas -> surgery, dentistry
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 2);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 3);
-- Rafael Ortega -> surgery
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (4, 2);
-- Henry Stevens -> radiology
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (5, 1);
