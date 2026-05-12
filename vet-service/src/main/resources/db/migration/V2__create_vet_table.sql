-- V2: Create the vet table for veterinarian records
CREATE TABLE vets (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(30)  NOT NULL,
    last_name  VARCHAR(30)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
