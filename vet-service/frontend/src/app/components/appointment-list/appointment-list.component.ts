import { Component, OnInit } from '@angular/core';
import { AppointmentService } from '../../services/appointment.service';
import { Appointment } from '../../models/vet.model';
import { MatSnackBar } from '@angular/material/snack-bar';

/**
 * Component displaying a list of all appointments in a Material table.
 * Supports cancelling and deleting appointments directly from the list.
 */
@Component({
  selector: 'app-appointment-list',
  templateUrl: './appointment-list.component.html',
  styleUrls: ['./appointment-list.component.scss']
})
export class AppointmentListComponent implements OnInit {
  appointments: Appointment[] = [];
  displayedColumns: string[] = ['id', 'petName', 'petOwnerName', 'vetName', 'specialtyName', 'appointmentDate', 'appointmentTime', 'status', 'actions'];
  loading = true;

  constructor(
    private appointmentService: AppointmentService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAppointments();
  }

  /** Fetch all appointments from the backend */
  loadAppointments(): void {
    this.loading = true;
    this.appointmentService.getAll().subscribe({
      next: (appointments) => {
        this.appointments = appointments;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open('Failed to load appointments', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  /** Cancel an appointment and refresh the list */
  cancelAppointment(id: number): void {
    this.appointmentService.cancel(id).subscribe({
      next: () => {
        this.snackBar.open('Appointment cancelled', 'Close', { duration: 3000 });
        this.loadAppointments();
      },
      error: () => {
        this.snackBar.open('Failed to cancel appointment', 'Close', { duration: 3000 });
      }
    });
  }

  /** Delete an appointment permanently and refresh the list */
  deleteAppointment(id: number): void {
    this.appointmentService.delete(id).subscribe({
      next: () => {
        this.snackBar.open('Appointment deleted', 'Close', { duration: 3000 });
        this.loadAppointments();
      },
      error: () => {
        this.snackBar.open('Failed to delete appointment', 'Close', { duration: 3000 });
      }
    });
  }
}
