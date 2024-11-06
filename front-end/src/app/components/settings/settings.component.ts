import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgForOf, NgIf } from '@angular/common';

@Component({
  selector: 'app-settings',
  standalone: true,
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css'],
  imports: [ReactiveFormsModule, FormsModule, NgForOf, NgIf]
})
export class SettingsComponent {
  authMode: 'login' | 'register' | 'guest' | null = null;
  authForm: FormGroup;
  rounds = 1;
  mode = 'normal';
  opponent = 'machine';

  constructor(private fb: FormBuilder, private router: Router) {
    // Formulario de autenticación reactivo
    this.authForm = this.fb.group({
      guestName: ['']
    });
  }

  setAuthMode(mode: 'login' | 'register' | 'guest'): void {
    this.authMode = mode;

    // Redirigir a login o register según el modo seleccionado
    if (mode === 'login') {
      this.router.navigate(['/login']);
    } else if (mode === 'register') {
      this.router.navigate(['/register']);
    } else if (mode === 'guest') {
      // Solo en modo invitado, habilitamos el campo "guestName"
      this.authForm.controls['guestName'].setValidators(Validators.required);
    } else {
      this.authForm.controls['guestName'].clearValidators();
    }

    this.authForm.controls['guestName'].updateValueAndValidity();
  }

  startGame(): void {
    if (this.authForm.valid && this.authMode === 'guest') {
      const gameData = {
        authData: { guestName: this.authForm.value.guestName },
        rounds: this.rounds,
        mode: this.mode,
        opponent: this.opponent,
      };

      // Enviar a la API cuando esté lista
      console.log('Data to send:', gameData);

      // Navegar a la vista de Play
      this.router.navigate(['/play']);
    }
  }
}
