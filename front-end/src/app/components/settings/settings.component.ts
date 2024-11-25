import { Component, OnInit, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { NavigationService } from '../../services/navigation.service';
import { GameService } from '../../services/game.service';
import { Subscription } from 'rxjs';
import { NgForOf, NgIf } from '@angular/common';
import {ActivatedRoute, RouterLink} from '@angular/router';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  standalone: true,
  imports: [
    NgIf,
    NgForOf,
    ReactiveFormsModule,
    FormsModule,
    RouterLink,
  ]
})
export class SettingsComponent implements OnInit {
  isLoggedIn: boolean = false;
  userName: string | null = null;
  userId: string | null = null;
  guestName: string | null = '';
  guestId: string | null = null;
  gameId: string | null = null;
  authForm: FormGroup;

  rounds: number = 3;
  mode: string = 'classic';
  opponent: string = 'machine';
  gameDetailsLoaded: boolean = false;
  player1name: string | null = null;

  showErrorMessage: boolean = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private navigationService: NavigationService,
    private gameService: GameService,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute
  ) {
    this.authForm = this.fb.group({
      guestName: ['',
        [Validators.required,
          Validators.maxLength(8),
          Validators.pattern('^[a-zA-Z0-9]*$')]]
    });
  }

  ngOnInit(): void {
    window.onpopstate = null;
    const isLogged = !!localStorage.getItem('userName');
    this.authService.closeGame(isLogged);

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.gameId = id;
        localStorage.setItem('gameId', id);
        this.fetchGameDetails(id);

      } else {
        this.gameDetailsLoaded = true;
      }
    });
    this.subscriptions.push(
      this.authService.isLoggedIn$.subscribe(status => {
        this.isLoggedIn = status;
      }),
      this.authService.userName$.subscribe(name => {
        this.userName = name;
      }),
      this.authService.userId$.subscribe(id => {
        this.userId = id;
      }),
      this.authService.guestName$.subscribe(name => {
        this.guestName = name;
      }),
      this.authService.guestId$.subscribe(id => {
        this.guestId = id;
      }),
    );

    this.checkLocalStorageData();
  }

  private checkLocalStorageData(): void {
    if (this.userName === null && localStorage.getItem("userName")) {
      this.userName = localStorage.getItem("userName");
      this.cdr.detectChanges();
    }
    if (this.userId === null && localStorage.getItem("userId")) {
      this.userId = localStorage.getItem("userId");
      this.cdr.detectChanges();
    }
    if (!this.isLoggedIn && localStorage.getItem("authToken")) {
      this.isLoggedIn = true;
      this.cdr.detectChanges();
    }
  }

  private fetchGameDetails(gameId: string): void {
    this.gameService.getGameDetails(gameId).subscribe({
      next: (details) => {
        const result = details.result;
        if (result !== "CONFIGURED") {
          if (result !== "ONGOING" && details.remainingRounds != details.rounds -1) {
            alert('This game is already started');
            this.navigationService.navigateTo('/');
          }
        }
        this.rounds = details.rounds;
        this.mode = this.formatMode(details.mode.toLowerCase());
        this.player1name = details.player1Name + " 🧑‍🚀";
        this.gameDetailsLoaded = true;
        this.cdr.detectChanges();
      },
      error: (err) => {
        alert('Error fetching game');
        this.gameDetailsLoaded = true;
        this.navigationService.navigateTo('/');
      }
    });
  }

  private formatMode(mode: string): string {
    const formatted = mode.charAt(0).toUpperCase() + mode.slice(1).toLowerCase();
    const emoji = mode.toLowerCase() === 'classic' ? '✋' : mode.toLowerCase() === 'spock' ? '🖖' : '';
    return `${formatted} ${emoji}`; //
  }

  startGame(): void {
    const isRealTime = this.opponent === "human";
    if (isRealTime) {
      localStorage.setItem("numPlayer", "1");
    }
    if (!this.isLoggedIn && this.authForm.get('guestName')?.valid) {
      const guestName = this.authForm.get('guestName')?.value;
      this.authService.guestPlayer(guestName).subscribe(response => {
        if (response.success) {
          if (response.userId) {
            this.createGameSettings(response.userId, isRealTime, this.gameId!);
          }
        }
      });
    } else if (!this.isLoggedIn && !this.authForm.get('guestName')?.valid) {
      this.setError();
    }
    if (this.userId) {
      const id = this.userId;
      this.createGameSettings(id, isRealTime, this.gameId!);
    }
  }

  private createGameSettings(id: string, realTime: boolean, gameId: string): void {
    const gameSettings = {
      player1Id: id,
      mode: this.mode.toUpperCase(),
      rounds: this.rounds,
      isRealTime: realTime,
    };
    if (!gameId) {
      this.gameService.startGame(gameSettings).subscribe({
        next: (response) => {
          if (response && response.success) {
            const route = realTime ? '/shared-game' : '/play';
            this.navigationService.navigateTo(route);
          } else {
            this.setError();
          }
        },
        error: () => this.setError()
      });
    } else {
      localStorage.setItem("numPlayer", "2");
      const player2Id = this.userId ? this.userId : this.guestId;
      if (player2Id && this.gameId) {
        const gameSettings = {
          gameId: this.gameId,
          player2Id: player2Id,
        };
        this.gameService.setGameForPlayer2(gameSettings).subscribe({
          next: (response) => {
            if (response.success) {
              this.navigationService.navigateTo('/play');
            }
          },
          error: () => this.setError()
        });
      } else {
        this.setError();
      }
    }
  }

  goBack(): void {
    this.navigationService.navigateTo('/');
  }

  setError(): void {
    this.showErrorMessage = true;
    setTimeout(() => this.showErrorMessage = false, 3000);
  }
}
