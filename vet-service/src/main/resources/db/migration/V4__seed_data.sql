-- Insert Specialties
INSERT INTO specialties (name) VALUES
('radiology'),
('surgery'),
('dentistry');

-- Insert Vets
INSERT INTO vets (first_name, last_name) VALUES
('James', 'Carter'),
('Helen', 'Leary'),
('Linda', 'Douglas'),
('Rafael', 'Ortega'),
('Henry', 'Stevens'),
('Sharon', 'Jenkins');

-- Link Vets to Specialties
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES
(2, 1),
(3, 2),
(3, 3),
(4, 2),
(5, 1);
