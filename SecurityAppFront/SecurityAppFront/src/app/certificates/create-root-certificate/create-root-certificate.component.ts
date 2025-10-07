import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { CertificatesService } from '../certificates.service';
import { AuthService } from 'src/app/auth/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CertificateRequestDTO } from '../model/CertificateRequestDTO.model';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-create-root-certificate',
  templateUrl: './create-root-certificate.component.html',
  styleUrls: ['./create-root-certificate.component.css'],
  animations: [
    trigger('slideIn', [
      state('void', style({ transform: 'translateY(0)', opacity: 0 })),
      transition(':enter', [
        animate('2.0s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
      ])
    ])
  ]
})
export class CreateRootCertificateComponent implements OnInit {

  rootForm!: FormGroup;
  userId!: number | null;

  constructor(
    private fb: FormBuilder,
    private certificateService: CertificatesService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.rootForm = this.fb.group({
      cn: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(64),
        Validators.pattern(/^[a-zA-Z0-9\s\.\-_]+$/)
      ]],
      o: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(64),
        Validators.pattern(/^[a-zA-Z0-9\s\.\-_&]+$/)
      ]],
      ou: ['', [
        Validators.minLength(2),
        Validators.maxLength(64),
        Validators.pattern(/^[a-zA-Z0-9\s\.\-_]+$/)
      ]],
      c: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(2),
        Validators.pattern(/^[A-Z]{2}$/)
      ]],
      durationInDays: [365, [
        Validators.required,
        Validators.min(1),
        Validators.max(7300)
      ]],
      isRoot: [true],
      isIntermediate: [false],
      isEndEntity: [false],
      isCA: [false],
      extensions: this.fb.control({})
    });

    const currentUser = this.authService.getCurrentUser();
    this.userId = currentUser ? currentUser.userId : null;
  }

  onSubmit() {
    if (this.rootForm.invalid) {
      this.rootForm.markAllAsTouched();
      this.snackBar.open('Please fix all errors before submitting', 'Close', {
        duration: 3000,
        horizontalPosition: 'center'
      });
      return;
    }

    if (this.userId !== null) {
      const dto: CertificateRequestDTO = {
        cn: this.rootForm.value.cn,
        o: this.rootForm.value.o,
        ou: this.rootForm.value.ou,
        c: this.rootForm.value.c,
        issuerId: null,
        durationInDays: this.rootForm.value.durationInDays,
        isRoot: true,
        isIntermediate: false,
        isEndEntity: false,
        isCA: this.rootForm.value.isCA,
        extensions: this.rootForm.value.extensions || {}
      };

      this.certificateService.issueCertificate(dto).subscribe({
        next: res => {
          console.log('Root certificate issued' + res);
          this.snackBar.open('Root certificate created', 'Close', {
            duration: 4000,
            horizontalPosition: 'center'
          });
          this.rootForm.reset({ durationInDays: 365, isCA: true });
        },
        error: err => {
          console.error('Error during making certificate', err);
          this.snackBar.open('Error during certificate issue', 'Close', {
            duration: 4000,
            horizontalPosition: 'center'
          });
        }
      });
    }
  }

  getErrorMessage(fieldName: string): string {
    const control = this.rootForm.get(fieldName);
    if (!control || !control.errors || !control.touched) return '';

    if (control.errors['required']) return `${fieldName} is required`;
    if (control.errors['minlength']) return `Minimum ${control.errors['minlength'].requiredLength} characters`;
    if (control.errors['maxlength']) return `Maximum ${control.errors['maxlength'].requiredLength} characters`;
    if (control.errors['min']) return `Minimum value is ${control.errors['min'].min}`;
    if (control.errors['max']) return `Maximum value is ${control.errors['max'].max}`;
    if (control.errors['pattern']) {
      if (fieldName === 'c') return 'Country must be 2 uppercase letters (e.g., RS, US)';
      if (fieldName === 'cn') return 'Only letters, numbers, spaces, dots, hyphens and underscores allowed';
      if (fieldName === 'o') return 'Only letters, numbers, spaces, dots, hyphens, underscores and & allowed';
      if (fieldName === 'ou') return 'Only letters, numbers, spaces, dots, hyphens and underscores allowed';
    }
    return '';
  }

  hasError(fieldName: string): boolean {
    const control = this.rootForm.get(fieldName);
    return !!(control && control.invalid && control.touched);
  }
}