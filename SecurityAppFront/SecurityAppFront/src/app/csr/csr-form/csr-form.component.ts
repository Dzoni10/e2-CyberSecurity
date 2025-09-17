import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CsrService } from '../csr.service';
import { CertificatesService } from 'src/app/certificates/certificates.service';

@Component({
  selector: 'app-csr-form',
  templateUrl: './csr-form.component.html',
})
export class CsrFormComponent implements OnInit {
  csrForm!: FormGroup;
  caList: any[] = []; // lista dostupnih CA (id + name)
  uploadStatus: string | null = null;

  constructor(private fb: FormBuilder, private http: HttpClient, private csrService: CsrService, private certificateService: CertificatesService) {}

  ngOnInit(): void {
    this.csrForm = this.fb.group({
      csrFile: [null, Validators.required],
      selectedCaId: [null, Validators.required],
      requestedDurationDays: [null, [Validators.required, Validators.min(1)]],
    });


    this.certificateService.getCACertificates().subscribe({
        next: (data) => (this.caList = data),
      error: (err) => console.error('Failed to load CA list', err),
    });
  }

  onFileChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.csrForm.patchValue({ csrFile: file });
    }
  }

  submitForm() {
    if (this.csrForm.invalid) {
      this.uploadStatus = 'Please fill in all fields correctly.';
      return;
    }

    const formData = new FormData();
    formData.append('csrFile', this.csrForm.get('csrFile')?.value);
    formData.append('selectedCaId', this.csrForm.get('selectedCaId')?.value);
    formData.append('requestedDurationDays', this.csrForm.get('requestedDurationDays')?.value);

    this.csrService.uploadCSR(formData).subscribe({
      next: (res) => {
        this.uploadStatus = res.status;
      },
      error: (err) => {
        this.uploadStatus = 'Upload failed: ' + (err.error?.message || 'Unknown error');
      },
    });
  }
}

