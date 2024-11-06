import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private apiUrl = 'http://localhost:8080/api';  // Cambiar al endpoint real cuando esté disponible

  constructor(private http: HttpClient) {}

  startGame(rounds: number, mode: string, opponent: string): Observable<any> {
    // TODO: Integrar con la API de Spring Boot
    const payload = { rounds, mode, opponent };
    return this.http.post(`${this.apiUrl}/start-game`, payload);
  }

  getGameHistory(): Observable<{ date: string, result: string }[]> {
    // TODO: Integrar con la API de Spring Boot
    return this.http.get<{ date: string, result: string }[]>(`${this.apiUrl}/history`);
  }
}
