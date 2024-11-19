import { Component, OnInit } from '@angular/core';
import { GameService } from '../../services/game.service';
import {NgForOf, NgIf, NgOptimizedImage, UpperCasePipe} from '@angular/common';
import {NavigationService} from '../../services/navigation.service';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-play',
  standalone: true,
  templateUrl: './play.component.html',
  imports: [NgForOf, NgIf, NgOptimizedImage, UpperCasePipe],
})
export class PlayComponent implements OnInit {
  gameId: string | null = null;
  realTime: boolean | null = null;
  result: string | null = null;
  rounds: number | null = null;
  mode: string | null = null;
  player1ID: string | null = null;
  player2ID: string | null = null;
  player1Name: string | null = null;
  player2Name: string | null = null;
  userName: string | null = null;
  opponentName: string | null = null;
  loading: boolean = true;
  loadingOpponent: boolean = false;
  roundResult: string | null = null;
  currentRound: number = 0;
  explanation: string | null = null;
  gameResult: string | null = null;
  moves = [
    { name: 'rock', image: 'rock.svg' },
    { name: 'paper', image: 'paper.svg' },
    { name: 'scissors', image: 'scissors.svg' },
    { name: 'lizard', image: 'lizard.svg' },
    { name: 'spock', image: 'spock.svg' }
  ];
  activeIndex = 0;
  userNum: string | null = null;
  player1Move: string | null = null;
  player2Move: string | null = null;
  player1Score: number = 0;
  player2Score: number = 0;
  draws: number = 0;

  constructor(
    private gameService: GameService,
    private navigationService: NavigationService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.gameId = localStorage.getItem('gameId');
    this.userNum = localStorage.getItem('numPlayer');
    history.pushState(null, '', location.href);
    window.onpopstate = function() {
      history.go(1);
    };
    this.currentRound =  Number(localStorage.getItem('currentRound') || 0);
    this.player1Score = Number(localStorage.getItem('player1Score')) || 0;
    this.player2Score = Number(localStorage.getItem('player2Score')) || 0;
    this.draws = Number(localStorage.getItem('draws')) || 0;

    if (this.gameId) {
      this.fetchGameDetails(this.gameId);
    } else {
      alert('No game found!');
      this.loading = false;
      this.closeGameAndRedirect()
    }
  }


  fetchGameDetails(gameId: string): void {
    this.loading = true;

    this.gameService.getGameDetails(gameId).subscribe({
      next: (response) => {
        if (response.success) {
          this.realTime = response.realTime;
          this.result = response.result;
          this.rounds = response.rounds;
          this.mode = response.mode;
          this.player1ID = response.player1ID;
          this.player2ID = response.player2ID;
          this.player1Name = response.player1Name;
          this.player2Name = response.player2Name;
          if (this.result !== "CONFIGURED" && this.result !== "ONGOING") {
            this.navigationService.navigateTo('/game-finished');
          }
          if (this.realTime) {
            if (this.userNum === "2") {
              this.opponentName = this.player1Name;
            }
            if (this.userNum === "1" && this.player2Name) {
              this.opponentName = this.player2Name;
            }
          }
          if (!this.realTime) {
            this.opponentName = 'MACHINE';
            this.player2Name = "Machine"
          }
          this.loading = false;
        } else {
          this.loading = false;
          alert(response.message);
        }
      },
      error: (err) => {
        this.loading = false;
        alert('Error fetching game details');
      },
      complete: () => {
        this.loading = false;
      },
    });
  }

  get filteredMoves(): { name: string; image: string }[] {
    if (this.mode === 'CLASSIC') {
      return this.moves.slice(0, 3);
    }
    return this.moves;
  }

  nextMove() {
    if (this.activeIndex < this.filteredMoves.length - 1) {
      this.activeIndex++;
    } else {
      this.activeIndex = 0;
    }
  }

  prevMove() {
    if (this.activeIndex > 0) {
      this.activeIndex--;
    } else {
      this.activeIndex = this.filteredMoves.length - 1;
    }
  }

  protected translateMove(move: { name: string; image: string }): string {
    const moveMap: { [key: string]: string } = {
      rock: 'ROCK',
      paper: 'PAPER',
      scissors: 'SCISSORS',
      lizard: 'LIZARD',
      spock: 'SPOCK'
    };

    return moveMap[move.name] || 'Unknown';
  }

  protected translateMoveToEmoji(move: string): string {
    const moveMap: { [key: string]: string } = {
      'ROCK': '🪨',
      'PAPER': '📄',
      'SCISSORS': '✂️',
      'LIZARD': '🦎',
      'SPOCK': '🖖'
    };
    return moveMap[move] || 'Unknown';
  }

  throwMove(): void {
    const userMove = this.translateMove(this.moves[this.activeIndex]);
    this.loading = true;

    if (!this.realTime) {
      this.gameService.playRoundMachine(this.gameId!, this.player1ID!, this.rounds!, userMove).subscribe({
        next: (response) => {
          this.loading = false;
          if (response.success) {
            this.getResults(response);
          }
        },
        error: (err) => {
          this.loading = false;
          alert('Error processing round');
        },
      });
    } else {
      this.gameService.playRoundHuman(this.gameId!, this.userNum!, this.currentRound!, userMove).subscribe({
        next: (response) => {
          if (response.success && response.playedBy2) {
            this.loading = false;
            this.getResults(response);
            if (!this.player2Name) {
              this.player2Name = response.player2Name;
              this.opponentName = this.player2Name;
            }
          } else if (response.success && !response.playedBy2) {
            this.loading = false;
            this.loadingOpponent = true;
            this.pollForOpponentMove(response.roundId);
          }
        },
        error: (err) => {
          this.loadingOpponent = false;
          alert('Error processing round');
        },
      });
    }
  }

  private pollForOpponentMove(roundId: string): void {
    const interval = setInterval(() => {
      this.gameService.checkOpponentMove(roundId).subscribe(response => {
        if (response.success && response.playedBy2) {
          clearInterval(interval);
          if (!this.player2Name) {
            this.player2Name = response.player2Name;
            this.opponentName = this.player2Name;
          }
          this.getResults(response);
          this.loadingOpponent = false;
        }
      });
    }, 3000);
  }

  private getResults(response: {
    remainingRounds: number;
    roundResult: string | null;
    player1Move: string | null;
    player2Move: string | null;
    gameResult: string | null;
  }) {
    this.roundResult = response.roundResult;
    this.player1Move = response.player1Move;
    this.player2Move = response.player2Move;
    this.currentRound = this.rounds! - response.remainingRounds;
    localStorage.setItem("currentRound", this.currentRound!.toString());
    this.explanation = this.getExplanation(this.player1Move!, this.player2Move!);
    this.gameResult = response.gameResult;

    if (this.roundResult === 'PLAYER1_WIN') {
      this.player1Score++;
    } else if (this.roundResult === 'PLAYER2_WIN') {
      this.player2Score++;
    } else if (this.roundResult === 'DRAW') {
      this.draws++;
    }

    localStorage.setItem('player1Score', this.player1Score.toString());
    localStorage.setItem('player2Score', this.player2Score.toString());
    localStorage.setItem('draws', this.draws.toString());
  }

  getRoundResultMessage(roundResult: string | null): string {
    if (!roundResult) {
      return 'Unknown result';
    }
    if (this.realTime) {
      if (roundResult === 'PLAYER1_WIN') {
        return this.userNum === '1'
          ? '🫵 You have won this round! 🎖️'
          : `🙌 ${this.player1Name} has won this round! 😞`;
      } else if (roundResult === 'PLAYER2_WIN') {
        return this.userNum === '2'
          ? '🫵 You have won this round! 🎖️'
          : `🙌 ${this.player2Name} has won this round! 😞`;
      } else if (roundResult === 'DRAW') {
        return 'It’s a tie! Both played the same move! 🤝';
      }
    } else {
      switch (roundResult) {
        case 'PLAYER1_WIN':
          return '🫵 You have won this round! 🎖️';
        case 'PLAYER2_WIN':
          return '🤖 The machine has won this round! 😭';
        case 'DRAW':
          return 'You and the machine have tied! 💃🕺';
        default:
          return 'Unknown result';
      }
    }
    return 'Unknown result';
  }

  getMoveExplanation(move: string): string {
    const classicExplains: { [key: string]: string } = {
      rock: 'Rock 🪨 crushes Scissors but Paper covers Rock',
      paper: 'Paper 📄 covers Rock but Scissors cuts Paper',
      scissors: 'Scissors ✂️ cuts Paper but Rock crushes Scissors',
    };

    const spockExplains: { [key: string]: string } = {
      rock: 'Rock 🪨 crushes Scissors and Lizard but is smashed by Spock and covered by Paper',
      paper: 'Paper 📄 covers Rock and disproves Spock but is cut by Scissors and eaten by Lizard',
      scissors: 'Scissors ✂️ cuts Paper and decapitates Lizard but is smashed by Rock and Spock',
      lizard: 'Lizard 🦎 eats Paper and poisons Spock but is crushed by Rock and decapitated by Scissors',
      spock: 'Spock 🖖 smashes Scissors and crushes Rock but is poisoned by Lizard and disproved by Paper',
    };
    if (this.mode === 'CLASSIC') {
      return classicExplains[move] || 'Unknown move';
    } else {
      return spockExplains[move] || 'Unknown move';
    }
  }

  getExplanation(userMove: string, machineMove: string): string {
    const outcomes: { [key: string]: { [key: string]: string } } = {
      'ROCK': {
        'ROCK': "It's a draw!",
        'PAPER': 'Paper covers Rock!',
        'SCISSORS': 'Rock crushes Scissors!',
        'LIZARD': 'Rock crushes Lizard!',
        'SPOCK': 'Spock smashes Rock!',
      },
      'PAPER': {
        'ROCK': 'Paper covers Rock!',
        'PAPER': "It's a draw!",
        'SCISSORS': 'Scissors cuts Paper!',
        'LIZARD': 'Lizard eats Paper!',
        'SPOCK': 'Paper disproves Spock!',
      },
      'SCISSORS': {
        'ROCK': 'Rock crushes Scissors!',
        'PAPER': 'Scissors cuts Paper!',
        'SCISSORS': "It's a draw!",
        'LIZARD': 'Scissors decapitates Lizard!',
        'SPOCK': 'Spock smashes Scissors!',
      },
      'LIZARD': {
        'ROCK': 'Rock crushes Lizard!',
        'PAPER': 'Lizard eats Paper!',
        'SCISSORS': 'Scissors decapitates Lizard!',
        'LIZARD': "It's a draw!",
        'SPOCK': 'Lizard poisons Spock!',
      },
      'SPOCK': {
        'ROCK': 'Spock smashes Rock!',
        'PAPER': 'Paper disproves Spock!',
        'SCISSORS': 'Spock smashes Scissors!',
        'LIZARD': 'Lizard poisons Spock!',
        'SPOCK': "It's a draw!",
      }
    };
    return outcomes[userMove][machineMove];
  }

  nextRound(): void {
    if (this.gameResult !== "CONFIGURED" && this.gameResult !== "ONGOING") {
      this.navigationService.navigateTo('/game-finished');
    } else {
      this.roundResult = null;
      this.player1Move = null;
      this.player2Move = null;
      this.explanation = null;
      this.activeIndex = 0;
    }
  }
  closeGameAndRedirect(): void {
    const isLogged = !!localStorage.getItem('userName');
    this.authService.closeGame(isLogged);
    this.navigationService.navigateTo('');
  }
}
