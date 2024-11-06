import {Component, NgIterable, OnInit} from '@angular/core';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  standalone: true,
  imports: [
    NgForOf
  ],
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {
  gameHistory: any[] = [];
  games: (NgIterable<unknown> & NgIterable<any>) | undefined | null;

  ngOnInit(): void {
    this.fetchGameHistory();
  }

  fetchGameHistory(): void {
    // Simulación de petición a la API
    try {
      // Aquí iría la llamada a la API
      // this.gameHistory = await apiService.getGameHistory();

      // Simulación de respuesta de API
      this.gameHistory = []; // Cambia esto por los datos de la API
    } catch (error) {
      console.log('Error fetching game history:', error); // Manejo del error
      this.gameHistory = []; // Asignación vacía para evitar errores en el template
    }
  }
}
