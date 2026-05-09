import { Component } from '@angular/core';

/**
 * Root application component.
 * Provides the top-level navigation toolbar and router outlet.
 */
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'PetClinic Vet Appointments';
}
