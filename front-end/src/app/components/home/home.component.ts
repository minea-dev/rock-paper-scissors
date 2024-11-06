import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  constructor(private router: Router) {}

  navigateToSettings(): void {
    this.router.navigate(['/settings']);
  }

  navigateToHistory(): void {
    this.router.navigate(['/history']);
  }
}
