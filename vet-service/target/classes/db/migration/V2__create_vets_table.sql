CREATE TABLE vets (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(30) NOT NULL,
    last_name  VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vet_specialties (
    vet_id       INT NOT NULL,
    specialty_id INT NOT NULL,
    PRIMARY KEY (vet_id, specialty_id),
    FOREIGN KEY (vet_id) REFERENCES vets (id) ON DELETE CASCADE,
    FOREIGN KEY (specialty_id) REFERENCES specialties (id) ON DELETE CASCADE
);

INSERT INTO vets (first_name, last_name) VALUES ('James', 'Carter');
INSERT INTO vets (first_name, last_name) VALUES ('Helen', 'Leary');
INSERT INTO vets (first_name, last_name) VALUES ('Linda', 'Douglas');
INSERT INTO vets (first_name, last_name) VALUES ('Rafael', 'Ortega');
INSERT INTO vets (first_name, last_name) VALUES ('Henry', 'Stevens');
INSERT INTO vets (first_name, last_name) VALUES ('Sharon', 'Jenkins');

INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (2, 1);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 2);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 3);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (4, 2);
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (5, 1);
