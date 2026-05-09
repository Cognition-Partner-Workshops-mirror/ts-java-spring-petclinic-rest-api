import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { VetService } from '../../services/vet.service';
import { SpecialtyService } from '../../services/specialty.service';
import { AppointmentService } from '../../services/appointment.service';
import { Vet, Specialty, AppointmentRequest } from '../../models/vet.model';
import { MatSnackBar } from '@angular/material/snack-bar';

/**
 * Form component for scheduling a new appointment.
 * Loads available vets and specialties for dropdown selection.
 * Validates required fields before submitting to the backend API.
 */
@Component({
  selector: 'app-appointment-form',
  templateUrl: './appointment-form.component.html',
  styleUrls: ['./appointment-form.component.scss']
})
export class AppointmentFormComponent implements OnInit {
  appointmentForm!: FormGroup;
  vets: Vet[] = [];
  specialties: Specialty[] = [];
  submitting = false;

  /** Minimum selectable date is today */
  minDate = new Date();

  constructor(
    private fb: FormBuilder,
    private vetService: VetService,
    private specialtyService: SpecialtyService,
    private appointmentService: AppointmentService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Initialize form with validation rules matching backend constraints
    this.appointmentForm = this.fb.group({
      petName: ['', [Validators.required, Validators.maxLength(100)]],
      petOwnerName: ['', [Validators.required, Validators.maxLength(100)]],
      vetId: [null, Validators.required],
      specialtyId: [null],
      appointmentDate: [null, Validators.required],
      appointmentTime: ['', Validators.required],
      reason: ['', [Validators.required, Validators.maxLength(500)]]
    });

    // Load vets and specialties for the dropdown selectors
    this.vetService.getAll().subscribe(vets => this.vets = vets);
    this.specialtyService.getAll().subscribe(specs => this.specialties = specs);
  }

  /** Submit the form to schedule a new appointment */
  onSubmit(): void {
    if (this.appointmentForm.invalid) {
      return;
    }

    this.submitting = true;
    const formValue = this.appointmentForm.value;

    // Format date to ISO string (yyyy-MM-dd) for the backend
    const dateObj: Date = formValue.appointmentDate;
    const formattedDate = `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}-${String(dateObj.getDate()).padStart(2, '0')}`;

    const request: AppointmentRequest = {
      petName: formValue.petName,
      petOwnerName: formValue.petOwnerName,
      vetId: formValue.vetId,
      specialtyId: formValue.specialtyId || null,
      appointmentDate: formattedDate,
      appointmentTime: formValue.appointmentTime,
      reason: formValue.reason
    };

    this.appointmentService.create(request).subscribe({
      next: () => {
        this.snackBar.open('Appointment scheduled successfully!', 'Close', { duration: 3000 });
        // Navigate to appointment list after successful creation
        this.router.navigate(['/appointments']);
      },
      error: (err) => {
        this.snackBar.open('Failed to schedule appointment. Please try again.', 'Close', { duration: 3000 });
        this.submitting = false;
      }
    });
  }
}
