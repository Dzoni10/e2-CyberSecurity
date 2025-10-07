import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../auth.service';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';
import * as zxcvbn from 'zxcvbn';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { AbstractControl, ValidationErrors } from '@angular/forms';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
  animations: [
    trigger('slideIn', [
      state('void', style({ transform: 'translateY(0)', opacity: 0 })),
      transition(':enter', [
        animate('2.0s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
      ])
    ])
  ]
})
export class SignupComponent implements OnInit {

  signupForm!: FormGroup;
  passwordStrength: number = 0;
  passwordFeedback: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.signupForm = this.fb.group({
      name: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z0-9À-ž\s\-'@]+$/)
      ]],
      surname: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z0-9À-ž\s\-'@]+$/)
      ]],
      email: ['', [
        Validators.required,
        Validators.email,
        Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(128),
        this.passwordStrengthValidator()
      ]],
      password2: ['', Validators.required],
      organization: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100),
        Validators.pattern(/^[a-zA-Z0-9\s\-\.&]+$/)
      ]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  passwordStrengthValidator() {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;

      const hasUpperCase = /[A-Z]/.test(value);
      const hasLowerCase = /[a-z]/.test(value);
      const hasNumeric = /[0-9]/.test(value);
      const hasSpecialChar = /[@$!%*?&]/.test(value);

      const passwordValid = hasUpperCase && hasLowerCase && hasNumeric && hasSpecialChar;
      return !passwordValid ? { passwordStrength: true } : null;
    };
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const password2 = control.get('password2');
    if (!password || !password2) return null;
    return password.value === password2.value ? null : { passwordMismatch: true };
  }

  register(): void {
    if (this.signupForm.invalid) {
      this.signupForm.markAllAsTouched();
      this.snackBar.open("Please fix all errors before submitting", "Close", {
        duration: 3000,
        horizontalPosition: "center"
      });
      return;
    }

    const { password2, ...payload } = this.signupForm.value;
    
    this.authService.register(payload).subscribe({
      next: res => {
        console.log("REGISTER SUCCESS");
        this.snackBar.open("Register successfully!", "Close", {
          duration: 3000,
          horizontalPosition: "center"
        });
        this.signupForm.reset();
        this.passwordStrength = 0;
      },
      error: err => {
        if (err.status === 201) {
          this.snackBar.open("Register successfully!", "Close", {
            duration: 3000,
            horizontalPosition: "center"
          });
          this.signupForm.reset();
          this.passwordStrength = 0;
        } else {
          this.snackBar.open("Cannot register!", "Close", {
            duration: 3000,
            horizontalPosition: "center"
          });
        }
      }
    });
  }

  checkPasswordStrength() {
    const pwd = this.signupForm.get('password')?.value || '';
    const result = zxcvbn(pwd);
    this.passwordStrength = result.score;
    this.passwordFeedback = result.feedback.suggestions.join(' ');
  }

  getErrorMessage(fieldName: string): string {
    const control = this.signupForm.get(fieldName);
    if (!control || !control.errors || !control.touched) return '';

    if (control.errors['required']) return `${fieldName} is required`;
    if (control.errors['minlength']) return `Minimum ${control.errors['minlength'].requiredLength} characters`;
    if (control.errors['maxlength']) return `Maximum ${control.errors['maxlength'].requiredLength} characters`;
    if (control.errors['email']) return 'Invalid email format';
    if (control.errors['pattern']) {
      if (fieldName === 'name' || fieldName === 'surname') return 'Only letters, numbers, spaces, hyphens, apostrophes and @ allowed';
      if (fieldName === 'organization') return 'Only letters, numbers, spaces, hyphens, dots and & allowed';
      return 'Invalid format';
    }
    if (control.errors['passwordStrength']) return 'Password must contain uppercase, lowercase, number and special character (@$!%*?&)';
    return '';
  }

  hasError(fieldName: string): boolean {
    const control = this.signupForm.get(fieldName);
    return !!(control && control.invalid && control.touched);
  }
}