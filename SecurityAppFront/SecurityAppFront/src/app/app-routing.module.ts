import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { SignupComponent } from './auth/signup/signup.component';
import { PassRecoveryComponent } from './auth/pass-recovery/pass-recovery.component';
import { SessionManagementComponent } from './auth/session-management/session-management.component';
import { CreateRootCertificateComponent } from './certificates/create-root-certificate/create-root-certificate.component';
import { CertificateListComponent } from './certificates/certificate-list/certificate-list.component';
import { CsrFormComponent } from './csr/csr-form/csr-form.component';
import { PasswordManagerComponent } from './password-manager/password-manager.component';
import { CreateIntCertificateComponent } from './certificates/create-int-certificate/create-int-certificate.component';
import { CreateEeCertificateComponent } from './certificates/create-ee-certificate/create-ee-certificate.component';
import { CaCertificateListComponent } from './certificates/ca-certificate-list/ca-certificate-list.component';
import { PendingCsrListComponent } from './csr/pending-csr-list/pending-csr-list.component';
import { EeCertificateListComponent } from './certificates/ee-certificate-list/ee-certificate-list.component';
import { CreateTemplateComponent } from './certificates/create-template/create-template.component';
const routes: Routes = [
   { path: 'login', component: LoginComponent },
   { path: 'signup', component: SignupComponent},
   { path: 'recovery', component:PassRecoveryComponent},
   { path: 'activeTokens', component:SessionManagementComponent},
   { path: 'createRootCertificate', component:CreateRootCertificateComponent},
   { path: 'certificateList', component:CertificateListComponent},
   { path: 'ca-certificateList', component:CaCertificateListComponent},
   { path: 'ee-certificateList', component:EeCertificateListComponent},
   { path: 'pending-csr-list', component:PendingCsrListComponent},
   { path: 'password-managment', component:PasswordManagerComponent},
   { path: 'csrForm', component:CsrFormComponent},
   { path: 'create-template', component:CreateTemplateComponent},
   {path: 'create-intermediate',component:CreateIntCertificateComponent},
   {path: 'create-endEntity', component:CreateEeCertificateComponent}
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
