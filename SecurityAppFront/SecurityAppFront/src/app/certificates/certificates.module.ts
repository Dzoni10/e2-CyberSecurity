import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CertificateListComponent } from './certificate-list/certificate-list.component';
import {MatCardModule} from '@angular/material/card'
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { CreateRootCertificateComponent } from './create-root-certificate/create-root-certificate.component'
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import {MatSlideToggleModule} from '@angular/material/slide-toggle'
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CreateIntCertificateComponent } from './create-int-certificate/create-int-certificate.component';
import { CreateEeCertificateComponent } from './create-ee-certificate/create-ee-certificate.component';

@NgModule({
  declarations: [
    CertificateListComponent,
    CreateRootCertificateComponent,
    CreateIntCertificateComponent,
    CreateEeCertificateComponent
  ],
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSlideToggleModule,
    MatInputModule,
    MatButtonModule
  ]
})
export class CertificatesModule { }
