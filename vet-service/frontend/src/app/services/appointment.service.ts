import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Appointment, AppointmentRequest } from '../models/vet.model';
import { environment } from '../../environments/environment';

/**
 * Service for interacting with the Appointment REST API endpoints.
 * Provides methods for CRUD operations, cancellation, and filtering appointments.
 */
@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private readonly baseUrl = `${environment.apiUrl}/appointments`;

  constructor(private http: HttpClient) {}

  /** Fetch all appointments */
  getAll(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(this.baseUrl);
  }

  /** Fetch a single appointment by ID */
  getById(id: number): Observable<Appointment> {
    return this.http.get<Appointment>(`${this.baseUrl}/${id}`);
  }

  /** Schedule a new appointment */
  create(appointment: AppointmentRequest): Observable<Appointment> {
    return this.http.post<Appointment>(this.baseUrl, appointment);
  }

  /** Update an existing appointment */
  update(id: number, appointment: AppointmentRequest): Observable<Appointment> {
    return this.http.put<Appointment>(`${this.baseUrl}/${id}`, appointment);
  }

  /** Cancel an appointment (sets status to CANCELLED) */
  cancel(id: number): Observable<Appointment> {
    return this.http.post<Appointment>(`${this.baseUrl}/${id}/cancel`, {});
  }

  /** Delete an appointment permanently */
  delete(id: number): Observable<Appointment> {
    return this.http.delete<Appointment>(`${this.baseUrl}/${id}`);
  }

  /** Filter appointments by vet ID */
  getByVetId(vetId: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(this.baseUrl, { params: { vetId: vetId.toString() } });
  }

  /** Filter appointments by date (ISO format yyyy-MM-dd) */
  getByDate(date: string): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(this.baseUrl, { params: { date } });
  }

  /** Filter appointments by status */
  getByStatus(status: string): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(this.baseUrl, { params: { status } });
  }
}
