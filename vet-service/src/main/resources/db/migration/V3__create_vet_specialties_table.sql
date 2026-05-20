-- Create the join table for the many-to-many relationship between vets and specialties
CREATE TABLE vet_specialties (
    vet_id       INT NOT NULL,
    specialty_id INT NOT NULL,
    PRIMARY KEY (vet_id, specialty_id),
    CONSTRAINT fk_vet_specialties_vet FOREIGN KEY (vet_id) REFERENCES vets (id) ON DELETE CASCADE,
    CONSTRAINT fk_vet_specialties_specialty FOREIGN KEY (specialty_id) REFERENCES specialties (id) ON DELETE CASCADE
);
