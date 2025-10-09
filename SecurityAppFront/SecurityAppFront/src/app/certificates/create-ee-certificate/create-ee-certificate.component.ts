import { Component, OnInit } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { CertificatesService } from '../certificates.service';
import { AuthService } from 'src/app/auth/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CertificateRequestDTO } from '../model/CertificateRequestDTO.model';
import { CertificateTemplate } from '../model/CertificateTemplate.model';
//import { MatSelectChange } from '@angular/material/select';

@Component({
  selector: 'app-create-ee-certificate',
  templateUrl: './create-ee-certificate.component.html',
  styleUrls: ['./create-ee-certificate.component.css'],
  animations: [
    trigger('slideIn', [
      state('void', style({ transform: 'translateY(0)', opacity: 0 })),
      transition(':enter', [
        animate('2.0s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
      ])
    ])
  ]
})
export class CreateEeCertificateComponent implements OnInit {

  endEntityForm!: FormGroup;
  userId!: number | null;
  issuerId!: number;
  maxDays!: number;
  templates: CertificateTemplate[] = [];
  selectedTemplate: CertificateTemplate | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private certificateService: CertificatesService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.issuerId = params['issuerId'];

      this.loadTemplates()

      this.certificateService.getCertificateById(this.issuerId).subscribe(issuer => {
        const today = new Date();
        const end = new Date(issuer.endDate);
        const diff = Math.floor((end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
        this.maxDays = diff;

        // Ažuriraj validator za durationInDays nakon što se učita maxDays
        this.endEntityForm.get('durationInDays')?.setValidators([
          Validators.required,
          Validators.min(1),
          Validators.max(this.maxDays)
        ]);
        this.endEntityForm.get('durationInDays')?.updateValueAndValidity();
      });
    });

    this.endEntityForm = this.fb.group({
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
      durationInDays: [1, [Validators.required, Validators.min(1)]],
      isRoot: [false],
      isIntermediate: [false],
      isEndEntity: [true],
      isCA: [false],
      extensions: this.fb.control({})
    });

    const currentUser = this.authService.getCurrentUser();
    this.userId = currentUser ? currentUser.userId : null;
  }

  loadTemplates(){
    this.certificateService.getAllTemplatesByIssuer(this.issuerId).subscribe({
      next: (d: CertificateTemplate[]) => {
      this.templates = d;
    },
    error: (err) => {
      console.error('Error loading templates', err);
      this.snackBar.open('Failed to load templates', 'Close', {
        duration: 3000,
        horizontalPosition: 'center'
      });
    }
  });
  }

  onTemplateSelect(event: any) : void {
  const templateId = event?.value;
  if (!templateId) {
    this.selectedTemplate = null;

    // resetuj validatore ako se deselectuje šablon
    this.endEntityForm.get('cn')?.setValidators([Validators.required]);
    this.endEntityForm.get('san')?.clearValidators();
    this.endEntityForm.get('cn')?.updateValueAndValidity();
    this.endEntityForm.get('san')?.updateValueAndValidity();
    return;
  }

  this.selectedTemplate = this.templates.find(t => t.id === templateId) || null;

  if (this.selectedTemplate) {
    // Patchuj TTL
    if (this.selectedTemplate.ttlDays)
      this.endEntityForm.patchValue({ durationInDays: this.selectedTemplate.ttlDays });

    // Validatori po regexu — samo ako postoje regexi u šablonu
    const cnValidators = [Validators.required];
    if (this.selectedTemplate.commonNameRegex) {
      try {
        cnValidators.push(Validators.pattern(new RegExp(this.selectedTemplate.commonNameRegex)));
      } catch {
        console.warn('Invalid CN regex in template');
      }
    }

    const sanValidators: any[] = [];
    if (this.selectedTemplate.subjectAltNameRegex) {
      try {
        sanValidators.push(Validators.pattern(new RegExp(this.selectedTemplate.subjectAltNameRegex)));
      } catch {
        console.warn('Invalid SAN regex in template');
      }
    }

    this.endEntityForm.get('cn')?.setValidators(cnValidators);
    this.endEntityForm.get('san')?.setValidators(sanValidators);

    this.endEntityForm.get('cn')?.updateValueAndValidity();
    this.endEntityForm.get('san')?.updateValueAndValidity();
  }
}

  onSubmit() {
    if (this.endEntityForm.invalid) {
      this.endEntityForm.markAllAsTouched();
      this.snackBar.open('Please fix all errors before submitting', 'Close', {
        duration: 3000,
        horizontalPosition: 'center'
      });
      return;
    }

    if (this.userId !== null) {
      const dto: CertificateRequestDTO = {
        cn: this.endEntityForm.value.cn,
        o: this.endEntityForm.value.o,
        ou: this.endEntityForm.value.ou,
        c: this.endEntityForm.value.c,
        issuerId: this.issuerId,
        durationInDays: this.endEntityForm.value.durationInDays,
        isRoot: false,
        isIntermediate: false,
        isEndEntity: true,
        isCA: false,
        extensions: this.endEntityForm.value.extensions || {}
      };

      this.certificateService.issueCertificate(dto).subscribe({
        next: res => {
          console.log('End entity certificate issued' + res);
          this.snackBar.open('End entity certificate created', 'Close', {
            duration: 4000,
            horizontalPosition: 'center'
          });
          this.endEntityForm.reset({ durationInDays: 1, isCA: false });
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
    const control = this.endEntityForm.get(fieldName);
    if (!control || !control.errors || !control.touched) return '';

    if (control.errors['required']) return `${fieldName} is required`;
    if (control.errors['minlength']) return `Minimum ${control.errors['minlength'].requiredLength} characters`;
    if (control.errors['maxlength']) return `Maximum ${control.errors['maxlength'].requiredLength} characters`;
    if (control.errors['min']) return `Minimum value is ${control.errors['min'].min}`;
    if (control.errors['max']) return `Maximum value is ${control.errors['max'].max} days (issuer validity)`;
    if (control.errors['pattern']) {
      if (fieldName === 'c') return 'Country must be 2 uppercase letters (e.g., RS, US)';
      if (fieldName === 'cn') return 'Only letters, numbers, spaces, dots, hyphens and underscores allowed';
      if (fieldName === 'o') return 'Only letters, numbers, spaces, dots, hyphens, underscores and & allowed';
      if (fieldName === 'ou') return 'Only letters, numbers, spaces, dots, hyphens and underscores allowed';
    }
    return '';
  }

  hasError(fieldName: string): boolean {
    const control = this.endEntityForm.get(fieldName);
    return !!(control && control.invalid && control.touched);
  }
}