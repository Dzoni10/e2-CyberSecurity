import { Component, OnInit } from '@angular/core';
import { trigger,state,style,animate,transition } from '@angular/animations';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../auth.service';
@Component({
  selector: 'app-create-ca-user',
  templateUrl: './create-ca-user.component.html',
  styleUrls: ['./create-ca-user.component.css'],
  animations: [
      trigger('slideIn', [
        state('void', style({ transform: 'translateY(0)', opacity: 0 })),
        transition(':enter', [
          animate('2.0s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
        ])
      ])
    ]
})
export class CreateCaUserComponent implements OnInit {

  caForm!:FormGroup

  constructor(private fb: FormBuilder,private snackBar: MatSnackBar,private authService: AuthService){}


  ngOnInit(): void {
    this.caForm = this.fb.group({
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
          organization:['',[
            Validators.required,
            Validators.pattern(/^[a-zA-Z0-9À-ž\s\-'@]+$/)
          ]],
          password:['Password1!']
        });
  }

  getErrorMessage(fieldName: string): string {
    const control = this.caForm.get(fieldName);
    if (!control || !control.errors || !control.touched) return '';

    if (control.errors['required']) return `${fieldName} is required`;
    if (control.errors['minlength']) return `Minimum ${control.errors['minlength'].requiredLength} characters`;
    if (control.errors['maxlength']) return `Maximum ${control.errors['maxlength'].requiredLength} characters`;
    if (control.errors['email']) return 'Invalid email format';
    if (control.errors['pattern']) {
      if (fieldName === 'name' || fieldName === 'surname' || fieldName ==='organization') return 'Only letters, numbers, spaces, hyphens, apostrophes and @ allowed';
      return 'Invalid format';
    }
    return '';
  }

  hasError(fieldName: string): boolean {
    const control = this.caForm.get(fieldName);
    return !!(control && control.invalid && control.touched);
  }

  register(): void {
    if (this.caForm.invalid) {
      this.caForm.markAllAsTouched();
      this.snackBar.open("Please fix all errors before submitting", "Close", {
        duration: 3000,
        horizontalPosition: "center"
      });
      return;
    }

    
    const {...payload } = this.caForm.value;
    
    this.authService.createCaUser(payload).subscribe({
      next: res => {
        console.log("REGISTER SUCCESS");
        this.snackBar.open("Register successfully!", "Close", {
          duration: 3000,
          horizontalPosition: "center"
        });
        this.caForm.reset();
      },
      error: err => {
        if (err.status === 201) {
          this.snackBar.open("Register successfully!", "Close", {
            duration: 3000,
            horizontalPosition: "center"
          });
          this.caForm.reset();
        } else {
          this.snackBar.open("Cannot register!", "Close", {
            duration: 3000,
            horizontalPosition: "center"
          });
        }
      }
    });
  }
}
