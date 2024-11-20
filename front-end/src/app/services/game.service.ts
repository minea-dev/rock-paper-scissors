import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class GameService {
  private apiUrl = environment.apiUrl + '/game';

  constructor(private http: HttpClient) {}

  /**
   * Starts a new game.
   * @param gameData - The data needed to start a new game.
   * @param gameData.player1Id - The ID of the first player.
   * @param gameData.mode - The game mode.
   * @param gameData.rounds - The total number of rounds.
   * @param gameData.isRealTime - Whether the game is real-time or not.
   * @returns Observable with the game start response.
   */
  public startGame(gameData: {
    player1Id: string;
    mode: string;
    rounds: number;
    isRealTime: boolean;
  }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/start`, gameData).pipe(
      tap((response) => {
        if (response.success && response.gameId) {
          localStorage.setItem('gameId', response.gameId);
        }
      })
    );
  }

  /**
   * Sets the second player for a game.
   * @param gameData - The game and player 2 data.
   * @param gameData.gameId - The game ID.
   * @param gameData.player2Id - The ID of the second player.
   * @returns Observable with the response.
   */
  public setGameForPlayer2(gameData: {
    gameId: string;
    player2Id: string;
  }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/set-player2`, gameData);
  }

  /**
   * Fetches the details of a game.
   * @param gameId - The ID of the game to fetch details for.
   * @returns Observable with game details.
   */
  public getGameDetails(gameId: string): Observable<any> {
    const headers = new HttpHeaders().set('gameId', gameId);
    return this.http.get<any>(`${this.apiUrl}/game-details`, { headers }).pipe(
      map((response) => {
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

  /**
   * Plays a round against the machine.
   * @param gameId - The ID of the game.
   * @param player1Id - The ID of player 1.
   * @param rounds - The total number of rounds.
   * @param player1Move - The move of player 1.
   * @returns Observable with the result of the round.
   */
  public playRoundMachine(
    gameId: string,
    player1Id: string,
    rounds: number,
    player1Move: string
  ): Observable<any> {
    const gameData = {
      gameId,
      player1Id,
      rounds,
      player1Move,
    };
    return this.http.post(`${this.apiUrl}/play-round-machine`, gameData);
  }

  /**
   * Generates a link to share the game.
   * @param gameId - The ID of the game.
   * @returns The URL to share the game.
   */
  public generateSharedGameLink(gameId: string): string {
    return environment.webUrl + `/settings/${gameId}`;
  }

  /**
   * Plays a round against another human player.
   * @param gameId - The ID of the game.
   * @param numPlayer - The player's number (1 or 2).
   * @param currentRound - The current round.
   * @param move - The player's move.
   * @returns Observable with the result of the round.
   */
  public playRoundHuman(
    gameId: string,
    numPlayer: string,
    currentRound: number,
    move: string
  ): Observable<any> {
    const gameData = { gameId, numPlayer, currentRound, move };
    return this.http
      .post<any>(`${this.apiUrl}/play-round-human`, gameData)
      .pipe(
        map((response) => {
          return response;
        }),
        catchError((error) => {
          console.error('Error in playRoundHuman:', error);
          throw new Error('Could not play the round');
        })
      );
  }

  /**
   * Checks the opponent's move in a round.
   * @param roundId - The ID of the round to check.
   * @returns Observable with the opponent's move details.
   */
  public checkOpponentMove(roundId: string): Observable<any> {
    return this.http
      .get<any>(`${this.apiUrl}/round-details`, {
        headers: { roundId: roundId },
      })
      .pipe(
        map((response) => {
          return response;
        }),
        catchError((error) => {
          console.error('Error in getRoundDetails:', error);
          throw new Error('Could not fetch round details');
        })
      );
  }

  /**
   * Allows a player to leave a game.
   * @param gameId - The ID of the game to leave.
   * @returns Observable with the response when leaving the game.
   */
  public leaveGame(gameId: string): Observable<any> {
    const headers = new HttpHeaders().set('gameId', gameId);
    return this.http.get<any>(`${this.apiUrl}/leave-game`, { headers }).pipe(
      map((response) => {
        return response;
      }),
      catchError((error) => {
        console.error('Error in leaveGame:', error);
        throw new Error('Could not leave the game');
      })
    );
  }

  /**
   * Checks the game result after a round.
   * @param gameId - The ID of the game.
   * @returns Observable with the game result.
   */
  public checkGameResult(gameId: string): Observable<any> {
    const headers = new HttpHeaders().set('gameId', gameId);
    return this.http.get<any>(`${this.apiUrl}/check-game-result`, { headers });
  }
}
