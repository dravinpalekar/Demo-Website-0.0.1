import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SignUpSuperAdmin } from './sign-up-super-admin';

describe('SignUpSuperAdmin', () => {
  let component: SignUpSuperAdmin;
  let fixture: ComponentFixture<SignUpSuperAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SignUpSuperAdmin],
    }).compileComponents();

    fixture = TestBed.createComponent(SignUpSuperAdmin);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
