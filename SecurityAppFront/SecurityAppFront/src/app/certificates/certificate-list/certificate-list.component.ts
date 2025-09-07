import { Component, OnInit } from '@angular/core';
import { CertificateResponse } from '../model/CertificateResponse.model';
import { CertificatesService } from '../certificates.service';

@Component({
  selector: 'app-certificate-list',
  templateUrl: './certificate-list.component.html',
  styleUrls: ['./certificate-list.component.css']
})
export class CertificateListComponent  implements OnInit{

  certificates: CertificateResponse[]=[];

  loading: boolean = true;

  constructor(private certifcateService: CertificatesService){}

  ngOnInit(): void {
    this.certifcateService.getCertificates().subscribe({
      next:(data) =>{
        this.certificates=data;
        this.loading=false;
      },error: ()=>{
        this.loading=true;
      }
    })
  }

}
