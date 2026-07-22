import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FindFriend } from './find-friend';

describe('FindFriend', () => {
  let component: FindFriend;
  let fixture: ComponentFixture<FindFriend>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FindFriend],
    }).compileComponents();

    fixture = TestBed.createComponent(FindFriend);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
