import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagePermission } from './manage-permission';

describe('ManagePermission', () => {
  let component: ManagePermission;
  let fixture: ComponentFixture<ManagePermission>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagePermission],
    }).compileComponents();

    fixture = TestBed.createComponent(ManagePermission);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
