-- Reset identity sequences past seed data to avoid conflicts
ALTER TABLE specialties ALTER COLUMN id RESTART WITH 100;
ALTER TABLE vets ALTER COLUMN id RESTART WITH 100;
