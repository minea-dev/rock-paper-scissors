import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../environments/environment';
import {catchError, map, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private apiUrl = environment.apiUrl + '/game';

  constructor(private http: HttpClient) {}

  public startGame(gameData: {
    player1Id: string;
    mode: string;
    rounds: number;
    isRealTime: boolean;
  }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/start`, gameData).pipe(
      tap(response => {
        if (response.success && response.gameId) {
          localStorage.setItem('gameId', response.gameId);
        }
      })
    );
  }

  public setGameForPlayer2(gameData: {
    gameId: string;
    player2Id: string;
  }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/set-player2`, gameData);
  }

  public getGameDetails(gameId: string): Observable<any> {
    const headers = new HttpHeaders().set('gameId', gameId);
    return this.http.get<any>(`${this.apiUrl}/game-details`, { headers }).pipe(
      map(response => {
        if (response.success) {
          return {
            success: response.success,
            message: response.message,
            realTime: response.realTime,
            result: response.result,
            rounds: response.rounds,
            mode: response.mode,
            player1ID: response.player1ID,
            player2ID: response.player2ID,
            player1Name: response.player1Name,
            player2Name: response.player2Name,
            currentRound: response.remainingRounds,
          };
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  public playRoundMachine(gameId: string, player1Id: string, rounds: number, player1Move: string): Observable<any> {
    const gameData = {
      gameId,
      player1Id,
      rounds,
      player1Move,
    };
    return this.http.post(`${this.apiUrl}/play-round-machine`, gameData);
  }

  public generateSharedGameLink(gameId: string): string{
    return environment.webUrl + `/settings/${gameId}`;
  }

  // Metodo para jugar la ronda contra oponente
  public playRoundHuman(gameId: string, numPlayer: string, currentRound: number, move: string): Observable<any> {
    const gameData = { gameId, numPlayer, currentRound, move };
    return this.http.post<any>(`${this.apiUrl}/play-round-human`, gameData)
      .pipe(
        map(response => {
          return response;
        }),
        catchError(error => {
          console.error('Error en playRoundHuman:', error);
          throw new Error('No se pudo jugar la ronda');
        })
      );
  }

  // Metodo para obtener detalles de la ronda
  public checkOpponentMove(roundId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/round-details`, {
      headers: { 'roundId': roundId }
    })
      .pipe(
        map(response => {
          return response;
        }),
        catchError(error => {
          console.error('Error en getRoundDetails:', error);
          throw new Error('No se pudieron obtener los detalles de la ronda');
        })
      );
  }


}
