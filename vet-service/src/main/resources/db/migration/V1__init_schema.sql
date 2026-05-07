-- V1: Initial schema for the Vet microservice
-- Creates specialty, vet, and vet_specialty join tables with audit fields

CREATE TABLE specialty (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(80)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vet (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(30)  NOT NULL,
    last_name  VARCHAR(30)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Many-to-many join table linking vets to their specialties
CREATE TABLE vet_specialty (
    vet_id       INT NOT NULL,
    specialty_id INT NOT NULL,
    PRIMARY KEY (vet_id, specialty_id),
    CONSTRAINT fk_vet_specialty_vet FOREIGN KEY (vet_id) REFERENCES vet(id) ON DELETE CASCADE,
    CONSTRAINT fk_vet_specialty_specialty FOREIGN KEY (specialty_id) REFERENCES specialty(id) ON DELETE CASCADE
);

-- Indexes for common query patterns
CREATE INDEX idx_vet_last_name ON vet(last_name);
CREATE INDEX idx_specialty_name ON specialty(name);
