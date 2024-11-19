import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  private userNameSubject = new BehaviorSubject<string | null>(null);
  private userIdSubject = new BehaviorSubject<string | null>(null);
  private guestNameSubject = new BehaviorSubject<string | null>(null);
  private guestIdSubject = new BehaviorSubject<string | null>(null);
  private apiUrl = environment.apiUrl + '/auth';

  constructor(
    private http: HttpClient
  ) {}
  public get isLoggedIn$(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }

  public get userName$(): Observable<string | null> {
    return this.userNameSubject.asObservable();
  }

  public get userId$(): Observable<string | null> {
    return this.userIdSubject.asObservable();
  }

  public get guestName$(): Observable<string | null> {
    return this.guestNameSubject.asObservable();
  }

  public get guestId$(): Observable<string | null> {
    return this.guestIdSubject.asObservable();
  }

  private saveLocalStorageUser(userId: string, userName: string, token: string ): void {
    localStorage.setItem('userId', userId);
    localStorage.setItem('userName', userName);
    localStorage.setItem('authToken', token);
  }

  public closeGame(isLoggedIn: boolean): void {

    if (isLoggedIn) {
      localStorage.removeItem('gameId');
      localStorage.removeItem('numPlayer');
      localStorage.removeItem('currentRound');
      localStorage.removeItem('player1Score');
      localStorage.removeItem('player2Score');
      localStorage.removeItem('draws');
    } else {
      localStorage.clear();
    }
  }

  public login(email: string, password: string): Observable<{ success: boolean; id?: string; name?: string; token?: string; message?: string }> {
    const loginData = { email, password };

    return this.http.post<{ success: boolean, id?: string, name?: string, token?: string, message?: string }>(`${this.apiUrl}/login`, loginData).pipe(
      tap(response => {
        if (response.success) {
          this.isLoggedInSubject.next(true);
          this.saveLocalStorageUser(response.id!, response.name!, response.token!);
        } else {
          this.isLoggedInSubject.next(false);
        }
      }),
      catchError(error => {
        this.isLoggedInSubject.next(false);
        return of({ success: false, message: error.message });
      })
    );
  }

  public register(username: string, email: string, password: string): Observable<{ success: boolean; message: string }> {
    const registerData = { username, email, password };

    return this.http.post<{ success: boolean, message?: string, id?: string, name?: string, token?: string }>(`${this.apiUrl}/register`, registerData).pipe(
      tap(response => {
        if (response.success && response.token) {
          this.isLoggedInSubject.next(true);
          this.saveLocalStorageUser(response.id!, response.name!, response.token!);
        }
      }),
      map(response => ({ success: response.success, message: response.message || 'User registered successfully.' })),
      catchError(error => {
        alert(`Error: ${error.message}`);
        return of({ success: false, message: 'Registration failed. Please try again.' });
      })
    );
  }

  public guestPlayer(username: string): Observable<{ success: boolean; userId?: string; userName?: string }> {
    const guestData = { username };
    return this.http.post<{ success: boolean, id: string, name: string, token: string }>(`${this.apiUrl}/guest-player`, guestData).pipe(
      tap(response => {
        if (response.success && response.token) {
          localStorage.setItem('guestId', response.id);
          localStorage.setItem('guestName', response.name);
          localStorage.setItem('authToken', response.token);

          this.guestIdSubject.next(response.id);
          this.guestNameSubject.next(response.name);
        }
      }),
      map(response => ({
        success: response.success,
        userId: response.id,
        userName: response.name
      })),
      catchError(error => {
        return of({ success: false });
      })
    );
  }

  public logout(): void {
    localStorage.clear();
    this.isLoggedInSubject.next(false);
  }

  public checkSession(): void {
    const authToken = localStorage.getItem('authToken');
    if (!authToken) {
      this.isLoggedInSubject.next(false);
      this.userNameSubject.next(null);
      this.userIdSubject.next(null);
      return;
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${authToken}`);
    this.http.get(`${this.apiUrl}/check-session`, { headers, responseType: 'text' }).pipe(
      tap(response => {
        this.isLoggedInSubject.next(true);
        this.userNameSubject.next(localStorage.getItem('userName'));
        this.userIdSubject.next(localStorage.getItem('userId'));
        this.guestIdSubject.next(localStorage.getItem('guestId'));
        this.guestIdSubject.next(localStorage.getItem('guestName'));
      }),
      catchError(error => {
        this.isLoggedInSubject.next(false);
        localStorage.clear();
        return of(false);
      })
    ).subscribe();
  }
}

