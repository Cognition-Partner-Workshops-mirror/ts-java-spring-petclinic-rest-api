-- Create the specialties table for veterinary specializations
CREATE TABLE specialties (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(80) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
