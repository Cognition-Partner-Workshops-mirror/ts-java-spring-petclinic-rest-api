import { Component, OnInit } from '@angular/core';
import { VetService } from '../../services/vet.service';
import { Vet } from '../../models/vet.model';
import { MatSnackBar } from '@angular/material/snack-bar';

/**
 * Component displaying a list of veterinarians in a Material table.
 * Supports viewing vet details and their assigned specialties.
 */
@Component({
  selector: 'app-vet-list',
  templateUrl: './vet-list.component.html',
  styleUrls: ['./vet-list.component.scss']
})
export class VetListComponent implements OnInit {
  vets: Vet[] = [];
  displayedColumns: string[] = ['id', 'firstName', 'lastName', 'specialties'];
  loading = true;

  constructor(
    private vetService: VetService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadVets();
  }

  /** Fetch all vets from the backend API */
  loadVets(): void {
    this.loading = true;
    this.vetService.getAll().subscribe({
      next: (vets) => {
        this.vets = vets;
        this.loading = false;
      },
      error: (err) => {
        this.snackBar.open('Failed to load veterinarians', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  /** Format specialty names into a comma-separated string for display */
  getSpecialtyNames(vet: Vet): string {
    if (!vet.specialties || vet.specialties.length === 0) {
      return 'None';
    }
    return vet.specialties.map(s => s.name).join(', ');
  }
}
