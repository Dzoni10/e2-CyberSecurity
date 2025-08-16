import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreateCertificateComponent } from './create-certificate/create-certificate.component';
import { CertificateListComponent } from './certificate-list/certificate-list.component';
import {MatCardModule} from '@angular/material/card'
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner'

@NgModule({
  declarations: [
    CreateCertificateComponent,
    CertificateListComponent,
  ],
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressSpinnerModule
  ]
})
export class CertificatesModule { }
