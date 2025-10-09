import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CertificateListComponent } from './certificate-list/certificate-list.component';
import {MatCardModule} from '@angular/material/card'
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { CreateRootCertificateComponent } from './create-root-certificate/create-root-certificate.component'
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import {MatSlideToggleModule} from '@angular/material/slide-toggle'
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CreateIntCertificateComponent } from './create-int-certificate/create-int-certificate.component';
import { CreateEeCertificateComponent } from './create-ee-certificate/create-ee-certificate.component';
import { CaCertificateListComponent } from './ca-certificate-list/ca-certificate-list.component';
import { EeCertificateListComponent } from './ee-certificate-list/ee-certificate-list.component';
import { MatOptionModule } from '@angular/material/core';
import {MatDividerModule} from '@angular/material/divider'
import {MatSelectModule} from '@angular/material/select'
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CreateTemplateComponent } from './create-template/create-template.component';
import{MatCheckboxModule} from '@angular/material/checkbox'
@NgModule({
  declarations: [
    CertificateListComponent,
    CreateRootCertificateComponent,
    CreateIntCertificateComponent,
    CreateEeCertificateComponent,
    CaCertificateListComponent,
    EeCertificateListComponent,
    CreateTemplateComponent
  ],
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSlideToggleModule,
    MatInputModule,
    MatButtonModule,
    FormsModule,
    MatOptionModule,
    MatDividerModule,
    MatSelectModule,
    MatSnackBarModule,
    MatCheckboxModule
  ]
})
export class CertificatesModule { }
