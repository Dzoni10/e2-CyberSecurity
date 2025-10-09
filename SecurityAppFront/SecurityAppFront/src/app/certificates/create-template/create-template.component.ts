import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { CertificatesService } from '../certificates.service';

@Component({
  selector: 'app-create-template',
  templateUrl: './create-template.component.html',
  styleUrls: ['./create-template.component.css']
})
export class CreateTemplateComponent {
issuerId!: number;
templateForm!: FormGroup;
  keyUsageOptions: string[] = [
    'digitalSignature',
    'nonRepudiation',
    'keyEncipherment',
    'dataEncipherment',
    'keyAgreement',
    'keyCertSign',
    'cRLSign'
  ];
  extKeyUsageOptions: string[] = [
    'serverAuth',
    'clientAuth',
    'codeSigning',
    'emailProtection',
    'timeStamping'
  ];

  constructor(
    private fb: FormBuilder,
    private certService: CertificatesService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.issuerId = params['issuerId'];
    });

    this.templateForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(50)]],
      ttlDays: [365, [Validators.required, Validators.min(1)]],
      commonNameRegex: ['^[a-zA-Z0-9.-]+$', Validators.required],
      subjectAltNameRegex: ['^[a-zA-Z0-9.-]+$', Validators.required],
      keyUsage: this.fb.array([]),
      extendedKeyUsage: this.fb.array([])
    });
  }

  onSubmit(): void {
    if (this.templateForm.invalid) {
      this.templateForm.markAllAsTouched();
      this.snackBar.open('Please fill all required fields correctly.', 'Close', {
        duration: 3000
      });
      return;
    }

    const dto = {
      ...this.templateForm.value,
      issuerId: this.issuerId // ovde dodaj
    };

    this.certService.createTemplate(dto).subscribe({
      next: () => {
        this.snackBar.open('Template successfully created!', 'Close', { duration: 3000 });
        this.templateForm.reset({ ttlDays: 365 });
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Error creating template.', 'Close', { duration: 3000 });
      }
    });
  }

  onCheckboxChange(event: any, controlName: 'keyUsage' | 'extendedKeyUsage') {
    const formArray: FormArray = this.templateForm.get(controlName) as FormArray;
    if (event.checked) {
      formArray.push(this.fb.control(event.source.value));
    } else {
      const index = formArray.controls.findIndex(x => x.value === event.source.value);
      formArray.removeAt(index);
    }
  }
}
