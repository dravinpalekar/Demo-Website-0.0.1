import { TestBed } from '@angular/core/testing';

import { SuperAdminServices } from './super-admin-services';

describe('SuperAdminServices', () => {
  let service: SuperAdminServices;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SuperAdminServices);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
