-- V3 (H2 only): Reset auto-increment sequences past the seeded IDs
ALTER TABLE specialties ALTER COLUMN id RESTART WITH 100;
ALTER TABLE vets ALTER COLUMN id RESTART WITH 100;
