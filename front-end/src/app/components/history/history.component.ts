import { Component, OnInit } from '@angular/core';
import { HistoryService } from '../../services/history.service';
import { AuthService } from '../../services/auth.service';
import { NgForOf, NgIf } from '@angular/common';
import {NavigationService} from '../../services/navigation.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  standalone: true,
  imports: [
    NgIf,
    NgForOf,
  ],
})
export class HistoryComponent implements OnInit {
  gameHistory: any[] = [];
  isLoggedIn: boolean = false;
  loading: boolean = false;
  currentPage: number = 1;
  pageSize: number = 5;
  totalGames: number = 0;
  totalPages: number = 0;
  userName: string | null = null;

  constructor(
    private historyService: HistoryService,
    private authService: AuthService,
    private navigationService: NavigationService
  ) {}

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe((status) => {
      this.isLoggedIn = status;
      if (this.isLoggedIn || localStorage.getItem('authToken')) {
        this.fetchGameHistory();
      }
    });
    this.userName = localStorage.getItem('userName');
  }

  fetchGameHistory(): void {
    const userId = localStorage.getItem('userId');
    if (userId) {
      this.loading = true;
      this.historyService.getUserGames(userId).subscribe({
        next: (data) => {
          if (data && Array.isArray(data) && data.length > 0) {
            this.totalGames = data.length;
            this.totalPages = Math.ceil(this.totalGames / this.pageSize);

            this.gameHistory = data.slice(
              (this.currentPage - 1) * this.pageSize,
              this.currentPage * this.pageSize
            );
          } else {
            this.gameHistory = [];
          }
        },
        error: (err) => {
        },
        complete: () => {
          this.loading = false;
        }
      });
    }
  }

  changePage(page: number): void {
    if (page > 0 && page <= this.totalPages) {
      this.currentPage = page;
      this.fetchGameHistory();
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
  }

  translateGameResult(result: string, player1: string, player2: string): string {
    return this.historyService.translateRoundResult(result, player1, player2, this.userName);
  }

  goBack(): void {
    this.navigationService.navigateTo('');
  }
}
