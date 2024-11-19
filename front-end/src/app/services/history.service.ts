import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class HistoryService {

  private apiUrl = environment.apiUrl + '/history';
  constructor(private http: HttpClient) {}

  /**
   * Fetches game details by game ID.
   * @param gameId - ID of the game.
   * @returns Observable containing game details.
   */
  public getGameDetails(gameId: string): Observable<any> {
    const headers = new HttpHeaders({ 'gameId': gameId });
    return this.http.get(`${this.apiUrl}/game-details`, { headers });
  }

  /**
   * Fetches all games and their rounds for a specific user ID.
   * @param userId - ID of the user.
   * @returns Observable containing games and rounds for the user.
   */
  public getUserGames(userId: string): Observable<any> {
    const headers = new HttpHeaders({ 'userId': userId });
    return this.http.get<any>(`${this.apiUrl}/user-games`, { headers }).pipe(
      map(response => response.games)
    );
  }

  public translateRoundResult(
    result: string,
    player1: string,
    player2: string,
    userName: string | null
  ): string {
    if (result === 'PLAYER1_WIN') {
      return userName === player1 ? `You won 🏆` : `${player1} won 😞`;
    }
    if (result === 'PLAYER2_WIN') {
      return userName === player2 ? `You won 🏆` : `${player2} won 😞`;
    }
    if (result === 'DRAW') {
      return 'Draw 🤝';
    }
    return 'Unfinished game 💔';
  }

}