import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateIntCertificateComponent } from './create-int-certificate.component';

describe('CreateIntCertificateComponent', () => {
  let component: CreateIntCertificateComponent;
  let fixture: ComponentFixture<CreateIntCertificateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateIntCertificateComponent]
    });
    fixture = TestBed.createComponent(CreateIntCertificateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
