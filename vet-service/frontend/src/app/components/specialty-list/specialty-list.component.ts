import { Component, OnInit } from '@angular/core';
import { SpecialtyService } from '../../services/specialty.service';
import { Specialty } from '../../models/vet.model';
import { MatSnackBar } from '@angular/material/snack-bar';

/**
 * Component displaying a list of veterinary specialties in a Material table.
 */
@Component({
  selector: 'app-specialty-list',
  templateUrl: './specialty-list.component.html',
  styleUrls: ['./specialty-list.component.scss']
})
export class SpecialtyListComponent implements OnInit {
  specialties: Specialty[] = [];
  displayedColumns: string[] = ['id', 'name'];
  loading = true;

  constructor(
    private specialtyService: SpecialtyService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadSpecialties();
  }

  /** Fetch all specialties from the backend API */
  loadSpecialties(): void {
    this.loading = true;
    this.specialtyService.getAll().subscribe({
      next: (specialties) => {
        this.specialties = specialties;
        this.loading = false;
      },
      error: (err) => {
        this.snackBar.open('Failed to load specialties', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }
}
