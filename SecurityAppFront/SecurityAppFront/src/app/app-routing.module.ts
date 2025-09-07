import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { SignupComponent } from './auth/signup/signup.component';
import { PassRecoveryComponent } from './auth/pass-recovery/pass-recovery.component';
import { SessionManagementComponent } from './auth/session-management/session-management.component';
import { CreateRootCertificateComponent } from './certificates/create-root-certificate/create-root-certificate.component';
import { CertificateListComponent } from './certificates/certificate-list/certificate-list.component';

const routes: Routes = [
   { path: 'login', component: LoginComponent },
   { path: 'signup', component: SignupComponent},
   { path: 'recovery', component:PassRecoveryComponent},
   { path: 'activeTokens', component:SessionManagementComponent},
   { path: 'createRootCertificate', component:CreateRootCertificateComponent},
   { path: 'certificateList', component:CertificateListComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
