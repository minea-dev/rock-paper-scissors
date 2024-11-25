import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { NavigationService } from '../../services/navigation.service';
import { NgIf } from '@angular/common';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  imports: [NgIf],
  standalone: true
})
export class HomeComponent implements OnInit {
  isLoggedIn = false;
  userName: string | null = null;
  private authSubscription: Subscription | null = null;

  constructor(
    private authService: AuthService,
    private navigationService: NavigationService
  ) {}

  ngOnInit(): void {
    window.onpopstate = null;
    const isLogged = !!localStorage.getItem('userName');
    this.authService.closeGame(isLogged);

    this.authSubscription = this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
    });
    this.authService.userName$.subscribe(username => {
      this.userName = username;
    });
    this.authService.checkSession();
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  public navigateTo(route: string): void {
    this.navigationService.navigateTo(route);
  }

  public logout(): void {
    this.authService.logout();
  }
}
