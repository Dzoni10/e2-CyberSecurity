import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/auth/auth.service';
import { CertificatesService } from '../certificates.service';
import { CertificateResponse } from '../model/CertificateResponse.model';

@Component({
  selector: 'app-ee-certificate-list',
  templateUrl: './ee-certificate-list.component.html',
  styleUrls: ['./ee-certificate-list.component.css']
})
export class EeCertificateListComponent {
certificates: CertificateResponse[]=[];
  
    loading: boolean = true;
  
    constructor(private certifcateService: CertificatesService, private router: Router, private authService: AuthService){}
  
    ngOnInit(): void {
      ///const currentUser = this.authService.getCurrentUser();
      //if (!currentUser) return;
      //const userId = currentUser.userId;
      this.certifcateService.getCACertificatesByUser().subscribe({
        next:(data) =>{
          this.certificates = data.map(cert => ({
            ...cert,
            showReasonInput: false,
            revocationReason: ''
      }));
          this.loading=false;
        },error: ()=>{
          this.loading=true;
        }
      })
    }
  
    revoke(certificate: CertificateResponse, reason: string): void {
  if (!reason) {
    alert('Please provide a reason for revocation.');
    return;
  }

  this.certifcateService.revokeCertificate(certificate.id, reason).subscribe({
    next: () => {
      alert('Certificate revoked successfully');
      certificate.revoked = true; // update local UI
    },
    error: (err) => {
      alert('Failed to revoke certificate: ' + err.error);
    }
  });
}
}
