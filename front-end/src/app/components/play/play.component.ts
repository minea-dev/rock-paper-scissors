import { Component } from '@angular/core';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-play',
  standalone: true,
  templateUrl: './play.component.html',
  imports: [
    NgForOf
  ],
  styleUrls: ['./play.component.css']
})
export class PlayComponent {
  moves = ['🪨', '📄', '✂️'];
  activeIndex = 1;

  nextMove(): void {
    this.activeIndex = (this.activeIndex + 1) % this.moves.length;
  }

  prevMove(): void {
    this.activeIndex = (this.activeIndex - 1 + this.moves.length) % this.moves.length;
  }

  throwMove(): void {
    const selectedMove = this.moves[this.activeIndex];
    console.log('Move selected:', selectedMove);

    // Lógica para enviar el movimiento a la API
  }
}
