import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginSuperAdmin } from './login-super-admin';

describe('LoginSuperAdmin', () => {
  let component: LoginSuperAdmin;
  let fixture: ComponentFixture<LoginSuperAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginSuperAdmin],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginSuperAdmin);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
