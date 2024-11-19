import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameFinishedComponent } from './game-finished.component';

describe('GameFinishedComponent', () => {
  let component: GameFinishedComponent;
  let fixture: ComponentFixture<GameFinishedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameFinishedComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GameFinishedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
