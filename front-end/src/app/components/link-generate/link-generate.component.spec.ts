import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinkGenerateComponent } from './link-generate.component';

describe('LinkGenerateComponent', () => {
  let component: LinkGenerateComponent;
  let fixture: ComponentFixture<LinkGenerateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LinkGenerateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LinkGenerateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
