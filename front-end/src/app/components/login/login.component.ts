import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NavigationService } from '../../services/navigation.service';
import { AuthService } from '../../services/auth.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    NgIf
  ],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private navigationService: NavigationService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  public onSubmit(): void {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;

      this.authService.login(email, password).subscribe(response => {
        if (response.success) {
          this.navigationService.goBack();
        } else {
          this.errorMessage = 'Incorrect credentials';
        }
      });
    } else {
      this.errorMessage = 'Please fill in both fields.';
    }
  }

  public isFieldInvalid(controlName: string): boolean {
    const control = this.loginForm.get(controlName);
    return !!(control?.touched && control?.invalid);
  }

  public goBack(): void {
    this.navigationService.goBack();
  }
}
