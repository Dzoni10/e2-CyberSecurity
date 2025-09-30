import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateEeCertificateComponent } from './create-ee-certificate.component';

describe('CreateEeCertificateComponent', () => {
  let component: CreateEeCertificateComponent;
  let fixture: ComponentFixture<CreateEeCertificateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateEeCertificateComponent]
    });
    fixture = TestBed.createComponent(CreateEeCertificateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
