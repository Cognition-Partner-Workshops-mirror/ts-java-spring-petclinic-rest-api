-- V3: Seed data for specialties and veterinarians for development/demo purposes

-- Insert common veterinary specialties
INSERT INTO specialties (name) VALUES ('Radiology');
INSERT INTO specialties (name) VALUES ('Surgery');
INSERT INTO specialties (name) VALUES ('Dentistry');
INSERT INTO specialties (name) VALUES ('Cardiology');
INSERT INTO specialties (name) VALUES ('Dermatology');
INSERT INTO specialties (name) VALUES ('Emergency Care');

-- Insert sample veterinarians
INSERT INTO vets (first_name, last_name) VALUES ('James', 'Carter');
INSERT INTO vets (first_name, last_name) VALUES ('Helen', 'Leary');
INSERT INTO vets (first_name, last_name) VALUES ('Linda', 'Douglas');
INSERT INTO vets (first_name, last_name) VALUES ('Rafael', 'Ortega');
INSERT INTO vets (first_name, last_name) VALUES ('Henry', 'Stevens');
INSERT INTO vets (first_name, last_name) VALUES ('Sharon', 'Jenkins');

-- Assign specialties to vets (vet_id, specialty_id)
-- Helen Leary -> Radiology
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (2, 1);
-- Linda Douglas -> Surgery, Dentistry
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 2);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 3);
-- Rafael Ortega -> Surgery
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (4, 2);
-- Henry Stevens -> Radiology, Cardiology
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (5, 1);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (5, 4);
-- Sharon Jenkins -> Dermatology, Emergency Care
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (6, 5);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (6, 6);
