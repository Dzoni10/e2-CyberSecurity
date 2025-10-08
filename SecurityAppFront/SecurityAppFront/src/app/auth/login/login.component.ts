import { Component, AfterViewInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { MatFormFieldControl } from '@angular/material/form-field';

declare const grecaptcha: any;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  animations: [
    trigger('slideIn', [
      state('void', style({ transform: 'translateY(0)', opacity: 0 })),
      transition(':enter', [
        animate('2.0s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
      ])
    ])
  ]
})
export class LoginComponent implements AfterViewInit {

  recaptchaToken: string | null = null;

  loginForm = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.email
    ]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8)
    ])
  });

  constructor(
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngAfterViewInit(): void {
    if (grecaptcha) {
      grecaptcha.render('recaptcha-container', {
        sitekey: '6Ldm3qUrAAAAADxjsnZHzG4HdMmsCjdKrgFHRmBB',
        callback: (response: string) => {
          this.recaptchaToken = response;
        }
      });
    }
  }

  login(): void {
    if (!this.recaptchaToken) {
      this.snackBar.open("Please verify you are not a robot", "Close", {
        duration: 3000,
        horizontalPosition: "center"
      });
      return;
    }

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      this.snackBar.open("Please enter valid email and password", "Close", {
        duration: 3000,
        horizontalPosition: "center"
      });
      return;
    }

    if (this.loginForm.value.email !== null) {
      this.authService.login(
        this.loginForm.get('email')!.value!,
        this.loginForm.get('password')!.value!
      ).subscribe({
        next: (res) => {
          this.authService.saveToken(res.accessToken);
          console.log("Login successfully");
          this.snackBar.open("Logged successfully!", "Close", {
            duration: 3000,
            horizontalPosition: "center"
          });
          this.loginForm.reset();
          const user = this.authService.getCurrentUser();

          if (user?.role === 'ROLE_ADMIN') {
            //this.router.navigate['']
          } else if (user?.role === 'ROLE_CA') {
            //this.router.navigate['']
          } else {
            //this.router.navigate['']
          }
        },
        error: (err) => {
          let message = '';

          switch (err.status) {
            case 404:
              message = "User with this credentials doesn't exist";
              break;
            case 406:
              message = "You must verify your account via your email";
              break;
            case 401:
              message = "Incorrect password";
              break;
            default:
              message = "Login failed. Please try again.";
          }
          this.openSnackBar(message);
        }
      });
    }
  }

  openSnackBar(message: string): void {
    this.snackBar.open(message, '', {
      duration: 4000,
      horizontalPosition: 'center'
    });
  }

  getErrorMessage(fieldName: string): string {
    const control = this.loginForm.get(fieldName);
    if (!control || !control.errors || !control.touched) return '';

    if (control.errors['required']) return `${fieldName} is required`;
    if (control.errors['email']) return 'Invalid email format';
    if (control.errors['minlength']) return 'Password too short';
    return '';
  }

  hasError(fieldName: string): boolean {
    const control = this.loginForm.get(fieldName);
    return !!(control && control.invalid && control.touched);
  }
}