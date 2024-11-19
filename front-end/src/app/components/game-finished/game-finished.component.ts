import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HistoryService } from '../../services/history.service';
import { AuthService } from '../../services/auth.service';
import {NavigationService} from '../../services/navigation.service';
import {Subscription} from 'rxjs';  // Importa AuthService

@Component({
  selector: 'app-game-finished',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game-finished.component.html',
})
export class GameFinishedComponent implements OnInit {
  gameId: string | null = null;
  gameDetails: any = null;
  loading = true;
  error: string | null = null;
  isLoggedIn: boolean = false;
  userName: string | null = null;
  guestName: string | null = null;
  private subscriptions: Subscription[] = [];

  constructor(
    private historyService: HistoryService,
    private authService: AuthService,
    private navigationService: NavigationService
  ) {}

  ngOnInit(): void {
    history.pushState(null, '', location.href);
    window.onpopstate = function() {
      history.go(1);
    };
    this.subscriptions.push(
      this.authService.isLoggedIn$.subscribe(status => {
        this.isLoggedIn = status;
      })
    );
    this.gameId = localStorage.getItem('gameId');
    this.userName = localStorage.getItem('userName');
    this.guestName = localStorage.getItem('guestName');

    if (this.gameId) {
      this.historyService.getGameDetails(this.gameId).subscribe({
        next: (data) => {
          this.gameDetails = data;
          this.loading = false;
          //this.authService.closeGame(this.isLoggedIn);
        },
        error: (err) => {
          this.error = 'Error retrieving game details.';
          this.loading = false;
        },
      });
    } else {
      this.error = 'No game ID found in localStorage.';
      this.loading = false;
    }
  }

  translateRoundResult(result: string, player1: string, player2: string): string {
    console.log(result, player1, player2, this.userName || this.guestName);
    return this.historyService.translateRoundResult(result, player1, player2, this.userName || this.guestName);
  }

  playAgain(){
    this.navigationService.navigateTo('/');
  }
}
