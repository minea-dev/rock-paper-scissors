import { Injectable } from '@angular/core';
import { Client, Message, Stomp } from '@stomp/stompjs';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  client: Client;
  private gameUpdateSubject: Subject<any> = new Subject<any>(); // Para emitir los cambios del juego a los componentes

  constructor() {
    // Configuración del cliente STOMP
    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws-game', // Ajustado para pruebas locales
      connectHeaders: {},
      debug: (str) => {
        console.log(str); // Para ver los logs de la conexión WebSocket
      },
      onConnect: () => {
        console.log('Connected to WebSocket');
        this.client.subscribe('/topic/game', (message) => {
          this.handleGameUpdate(JSON.parse(message.body));
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error: ', frame);
      },
    });

    this.client.activate();
  }

  public handleGameUpdate(data: any): void {
    console.log('Game update received:', data);
    this.gameUpdateSubject.next(data); // Emitir el nuevo estado del juego
  }

  get gameUpdate$() {
    return this.gameUpdateSubject.asObservable();
  }

  sendMove(move: string): void {
    if (this.client.connected) {
      this.client.publish({
        destination: '/app/game/move',
        body: JSON.stringify({ move }),
      });
    } else {
      console.error('No WebSocket connection available');
    }
  }
}
