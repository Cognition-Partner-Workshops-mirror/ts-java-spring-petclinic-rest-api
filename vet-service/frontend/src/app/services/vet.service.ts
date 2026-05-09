import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Vet, VetRequest } from '../models/vet.model';
import { environment } from '../../environments/environment';

/**
 * Service for interacting with the Vet REST API endpoints.
 * Provides methods for CRUD operations and filtering vets.
 */
@Injectable({
  providedIn: 'root'
})
export class VetService {
  private readonly baseUrl = `${environment.apiUrl}/vets`;

  constructor(private http: HttpClient) {}

  /** Fetch all vets from the backend */
  getAll(): Observable<Vet[]> {
    return this.http.get<Vet[]>(this.baseUrl);
  }

  /** Fetch a single vet by ID */
  getById(id: number): Observable<Vet> {
    return this.http.get<Vet>(`${this.baseUrl}/${id}`);
  }

  /** Create a new vet */
  create(vet: VetRequest): Observable<Vet> {
    return this.http.post<Vet>(this.baseUrl, vet);
  }

  /** Update an existing vet */
  update(id: number, vet: VetRequest): Observable<Vet> {
    return this.http.put<Vet>(`${this.baseUrl}/${id}`, vet);
  }

  /** Delete a vet by ID */
  delete(id: number): Observable<Vet> {
    return this.http.delete<Vet>(`${this.baseUrl}/${id}`);
  }

  /** Search vets by last name */
  searchByLastName(lastName: string): Observable<Vet[]> {
    return this.http.get<Vet[]>(this.baseUrl, { params: { lastName } });
  }
}
