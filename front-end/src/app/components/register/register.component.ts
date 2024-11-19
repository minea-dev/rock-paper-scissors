import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { NavigationService } from '../../services/navigation.service';
import { NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf,
    RouterLink
  ],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private navigationService: NavigationService
  ) {

    this.registerForm = this.fb.group({
      username: ['', [
        Validators.required,
        Validators.maxLength(9),
        Validators.pattern(/^[a-zA-Z0-9]*$/)
      ]],
      email: ['', [
        Validators.required,
        Validators.email
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(16),
        Validators.pattern(/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)[A-Za-z\d]{8,16}$/)
      ]]
    });
  }

  public onSubmit(): void {
    if (this.registerForm.valid) {
      const { username, email, password } = this.registerForm.value;
      this.authService.register(username, email, password).subscribe(response => {
        if (response.success) {
          this.navigationService.goBack();
        } else {
          console.log(response);
          this.errorMessage = response.message;
        }
      });
    }
  }

  public isFieldInvalid(controlName: string): boolean {
    const control = this.registerForm.get(controlName);
    return !!(control?.touched && control?.invalid);
  }

  public getPasswordErrorMessage(): string {
    const passwordControl = this.registerForm.get('password');
    if (passwordControl?.hasError('required')) return 'Password is required';
    if (passwordControl?.hasError('minlength')) return 'Password must be at least 8 characters long';
    if (passwordControl?.hasError('maxlength')) return 'Password cannot exceed 16 characters';
    if (passwordControl?.hasError('pattern')) return 'Password must contain at least one uppercase letter, one lowercase letter, and one number';
    return '';
  }

  public getUsernameErrorMessage(): string {
    const usernameControl = this.registerForm.get('username');
    if (usernameControl?.hasError('required')) return 'Username is required';
    if (usernameControl?.hasError('maxlength')) return 'Username cannot exceed 9 characters';
    if (usernameControl?.hasError('pattern')) return 'Username cannot contain spaces or special characters';
    return '';
  }

  public goBack(): void {
    this.navigationService.goBack();
  }
}

