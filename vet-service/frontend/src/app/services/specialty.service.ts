import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Specialty, SpecialtyRequest } from '../models/vet.model';
import { environment } from '../../environments/environment';

/**
 * Service for interacting with the Specialty REST API endpoints.
 * Provides methods for CRUD operations on veterinary specialties.
 */
@Injectable({
  providedIn: 'root'
})
export class SpecialtyService {
  private readonly baseUrl = `${environment.apiUrl}/specialties`;

  constructor(private http: HttpClient) {}

  /** Fetch all specialties */
  getAll(): Observable<Specialty[]> {
    return this.http.get<Specialty[]>(this.baseUrl);
  }

  /** Fetch a single specialty by ID */
  getById(id: number): Observable<Specialty> {
    return this.http.get<Specialty>(`${this.baseUrl}/${id}`);
  }

  /** Create a new specialty */
  create(specialty: SpecialtyRequest): Observable<Specialty> {
    return this.http.post<Specialty>(this.baseUrl, specialty);
  }

  /** Update an existing specialty */
  update(id: number, specialty: SpecialtyRequest): Observable<Specialty> {
    return this.http.put<Specialty>(`${this.baseUrl}/${id}`, specialty);
  }

  /** Delete a specialty */
  delete(id: number): Observable<Specialty> {
    return this.http.delete<Specialty>(`${this.baseUrl}/${id}`);
  }
}
