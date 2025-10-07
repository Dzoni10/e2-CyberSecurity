import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CsrService } from '../csr.service';
import { CertificatesService } from 'src/app/certificates/certificates.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-csr-form',
  styleUrls: ['./csr-form.component.css'],
  templateUrl: './csr-form.component.html',
})
export class CsrFormComponent implements OnInit {
  csrForm!: FormGroup;
  caList: any[] = [];
  uploadStatus: string | null = null;
  maxDuration: number = 3650; // Default 10 godina

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private csrService: CsrService,
    private certificateService: CertificatesService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.csrForm = this.fb.group({
      csrFile: [null, [Validators.required, this.fileExtensionValidator(['.csr', '.pem'])]],
      privateKeyFile: [null, [Validators.required, this.fileExtensionValidator(['.pem', '.key'])]],
      selectedCaId: [null, Validators.required],
      requestedDurationDays: [365, [
        Validators.required,
        Validators.min(1),
        Validators.max(this.maxDuration)
      ]]
    });

    this.certificateService.getCACertificates().subscribe({
      next: (data) => {
        this.caList = data;
      },
      error: (err) => {
        console.error('Failed to load CA list', err);
        this.snackBar.open('Failed to load CA certificates', 'Close', {
          duration: 3000,
          horizontalPosition: 'center'
        });
      }
    });

    // Update max duration when CA is selected
    this.csrForm.get('selectedCaId')?.valueChanges.subscribe(caId => {
      if (caId) {
        const selectedCA = this.caList.find(ca => ca.id === +caId);
        if (selectedCA) {
          const today = new Date();
          const end = new Date(selectedCA.endDate);
          const diff = Math.floor((end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
          this.maxDuration = diff;

          // Update validator
          this.csrForm.get('requestedDurationDays')?.setValidators([
            Validators.required,
            Validators.min(1),
            Validators.max(this.maxDuration)
          ]);
          this.csrForm.get('requestedDurationDays')?.updateValueAndValidity();
        }
      }
    });
  }

  /**
   * Custom validator: Provera ekstenzije fajla
   */
  fileExtensionValidator(allowedExtensions: string[]) {
    return (control: AbstractControl): ValidationErrors | null => {
      const file = control.value;
      if (!file) return null;

      const fileName = file.name.toLowerCase();
      const isValid = allowedExtensions.some(ext => fileName.endsWith(ext));

      return isValid ? null : { invalidExtension: { allowed: allowedExtensions } };
    };
  }

  onCSRFileChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Proveri veličinu fajla (max 1MB)
      if (file.size > 1024 * 1024) {
        this.snackBar.open('CSR file must be smaller than 1MB', 'Close', {
          duration: 3000,
          horizontalPosition: 'center'
        });
        event.target.value = ''; // Reset input
        return;
      }

      this.csrForm.patchValue({ csrFile: file });
      this.csrForm.get('csrFile')?.markAsTouched();
      this.csrForm.get('csrFile')?.updateValueAndValidity();
    }
  }

  onPrivateKeyFileChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Proveri veličinu fajla (max 1MB)
      if (file.size > 1024 * 1024) {
        this.snackBar.open('Private key file must be smaller than 1MB', 'Close', {
          duration: 3000,
          horizontalPosition: 'center'
        });
        event.target.value = ''; // Reset input
        return;
      }

      this.csrForm.patchValue({ privateKeyFile: file });
      this.csrForm.get('privateKeyFile')?.markAsTouched();
      this.csrForm.get('privateKeyFile')?.updateValueAndValidity();
    }
  }

  submitForm() {
    if (this.csrForm.invalid) {
      this.csrForm.markAllAsTouched();
      this.snackBar.open('Please fill in all fields correctly', 'Close', {
        duration: 3000,
        horizontalPosition: 'center'
      });
      return;
    }

    const formData = new FormData();
    formData.append('csrFile', this.csrForm.get('csrFile')?.value);
    formData.append('privateKeyFile', this.csrForm.get('privateKeyFile')?.value);
    formData.append('selectedCaId', this.csrForm.get('selectedCaId')?.value);
    formData.append('requestedDurationDays', this.csrForm.get('requestedDurationDays')?.value);

    this.csrService.uploadCSR(formData).subscribe({
      next: (res) => {
        this.uploadStatus = res.status;
        this.snackBar.open('CSR uploaded successfully!', 'Close', {
          duration: 4000,
          horizontalPosition: 'center'
        });
        this.csrForm.reset({ requestedDurationDays: 365 });
      },
      error: (err) => {
        this.uploadStatus = 'Upload failed: ' + (err.error?.message || 'Unknown error');
        this.snackBar.open(this.uploadStatus, 'Close', {
          duration: 4000,
          horizontalPosition: 'center'
        });
      }
    });
  }

  getErrorMessage(fieldName: string): string {
    const control = this.csrForm.get(fieldName);
    if (!control || !control.errors || !control.touched) return '';

    if (control.errors['required']) return `${fieldName} is required`;
    if (control.errors['min']) return `Minimum value is ${control.errors['min'].min}`;
    if (control.errors['max']) return `Maximum value is ${control.errors['max'].max} days`;
    if (control.errors['invalidExtension']) {
      const allowed = control.errors['invalidExtension'].allowed.join(', ');
      return `Allowed extensions: ${allowed}`;
    }
    return '';
  }

  hasError(fieldName: string): boolean {
    const control = this.csrForm.get(fieldName);
    return !!(control && control.invalid && control.touched);
  }

  getFileName(controlName: string): string {
    const file = this.csrForm.get(controlName)?.value;
    return file ? file.name : 'No file chosen';
  }
}