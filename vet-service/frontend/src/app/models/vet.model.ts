// Model interfaces for Vet domain objects matching the backend API DTOs

/** Specialty as returned from the API */
export interface Specialty {
  id: number;
  name: string;
}

/** Request payload for creating/updating a specialty */
export interface SpecialtyRequest {
  name: string;
}

/** Vet as returned from the API, includes assigned specialties */
export interface Vet {
  id: number;
  firstName: string;
  lastName: string;
  specialties: Specialty[];
}

/** Request payload for creating/updating a vet */
export interface VetRequest {
  firstName: string;
  lastName: string;
  specialtyIds: number[];
}

/** Appointment as returned from the API */
export interface Appointment {
  id: number;
  petName: string;
  petOwnerName: string;
  vetId: number;
  vetName: string;
  specialtyId: number | null;
  specialtyName: string | null;
  appointmentDate: string;   // ISO date string (yyyy-MM-dd)
  appointmentTime: string;   // Time string (HH:mm)
  reason: string;
  status: string;            // SCHEDULED | CONFIRMED | CANCELLED | COMPLETED
}

/** Request payload for creating/updating an appointment */
export interface AppointmentRequest {
  petName: string;
  petOwnerName: string;
  vetId: number;
  specialtyId: number | null;
  appointmentDate: string;
  appointmentTime: string;
  reason: string;
}
