import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8080';  // URL del backend

  constructor(private http: HttpClient) {}

  getData(): Observable<any> {
    return this.http.get(`${this.apiUrl}/endpoint`);  // Cambia /endpoint al endpoint que desees
  }

  postData(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/endpoint`, data);
  }
}
