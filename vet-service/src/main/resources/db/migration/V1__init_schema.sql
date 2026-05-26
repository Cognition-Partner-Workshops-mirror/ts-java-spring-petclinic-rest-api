-- ===========================================================================
-- V1: Initial schema for the Vet microservice
-- Creates tables for specialties, vets, and the join table vet_specialties.
-- ===========================================================================

-- Specialties table
CREATE TABLE specialties (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(80)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vets table
CREATE TABLE vets (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(30)  NOT NULL,
    last_name  VARCHAR(30)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Many-to-many join table linking vets to their specialties
CREATE TABLE vet_specialties (
    vet_id       INT NOT NULL,
    specialty_id INT NOT NULL,
    PRIMARY KEY (vet_id, specialty_id),
    CONSTRAINT fk_vet_specialties_vet
        FOREIGN KEY (vet_id) REFERENCES vets (id) ON DELETE CASCADE,
    CONSTRAINT fk_vet_specialties_specialty
        FOREIGN KEY (specialty_id) REFERENCES specialties (id) ON DELETE CASCADE
);

-- Index for fast lookup of vets by specialty
CREATE INDEX idx_vet_specialties_specialty ON vet_specialties (specialty_id);
