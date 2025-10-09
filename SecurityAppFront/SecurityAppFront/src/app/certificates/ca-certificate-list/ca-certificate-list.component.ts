import { Component } from '@angular/core';
import { CertificateResponse } from '../model/CertificateResponse.model';
import { Router } from '@angular/router';
import { CertificatesService } from '../certificates.service';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-ca-certificate-list',
  templateUrl: './ca-certificate-list.component.html',
  styleUrls: ['./ca-certificate-list.component.css']
})
export class CaCertificateListComponent {
  certificates: CertificateResponse[]=[];
  
    loading: boolean = true;
  
    constructor(private certifcateService: CertificatesService, private router: Router, private authService: AuthService){}
  
    ngOnInit(): void {
      ///const currentUser = this.authService.getCurrentUser();
      //if (!currentUser) return;
      //const userId = currentUser.userId;
      this.certifcateService.getCACertificatesByOrg().subscribe({
        next:(data) =>{
          this.certificates=data;
          this.loading=false;
        },error: ()=>{
          this.loading=true;
        }
      })
    }
  
    createIntermediate(certificate: CertificateResponse): void{
      this.router.navigate(['create-intermediate'],{
        queryParams:{issuerId:certificate.id}
      });
    }
  
    createEndEntity(certificate: CertificateResponse): void{
      this.router.navigate(['create-endEntity'],{
        queryParams:{issuerId:certificate.id}
      });
    }

    createTemplate(cert: any) {
      this.router.navigate(['create-template'], {
      queryParams: { issuerId: cert.id }
    });
}
}
