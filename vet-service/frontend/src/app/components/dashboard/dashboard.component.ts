import { Component, OnInit } from '@angular/core';
import { VetService } from '../../services/vet.service';
import { SpecialtyService } from '../../services/specialty.service';
import { AppointmentService } from '../../services/appointment.service';
import { Vet, Specialty, Appointment } from '../../models/vet.model';

/**
 * Dashboard component showing summary statistics.
 * Displays counts of vets, specialties, and upcoming appointments.
 */
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  vetCount = 0;
  specialtyCount = 0;
  appointmentCount = 0;
  upcomingAppointments: Appointment[] = [];

  constructor(
    private vetService: VetService,
    private specialtyService: SpecialtyService,
    private appointmentService: AppointmentService
  ) {}

  ngOnInit(): void {
    // Load summary data for dashboard cards
    this.vetService.getAll().subscribe(vets => this.vetCount = vets.length);
    this.specialtyService.getAll().subscribe(specs => this.specialtyCount = specs.length);
    this.appointmentService.getAll().subscribe(appts => {
      this.appointmentCount = appts.length;
      // Show only upcoming (SCHEDULED) appointments, limited to 5
      this.upcomingAppointments = appts
        .filter(a => a.status === 'SCHEDULED')
        .slice(0, 5);
    });
  }
}
