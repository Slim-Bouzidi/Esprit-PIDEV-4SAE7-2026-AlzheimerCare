import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DoctorNotificationMessage {
  notificationId?: number;
  destinataireId?: number;
  type?: string;
  titre?: string;
  message?: string;
  referenceType?: string;
  referenceId?: number;
  dateCreation?: string;
}

@Injectable({ providedIn: 'root' })
export class DoctorNotificationWsService {
  private client?: Client;
  private subscription?: StompSubscription;
  private readonly notificationsSubject = new Subject<DoctorNotificationMessage>();

  notifications$: Observable<DoctorNotificationMessage> = this.notificationsSubject.asObservable();

  connect(): void {
    if (this.client?.active) return;

    // Connect directly to assistance-quotidienne service (port 8098) for WebSocket
    const wsUrl = 'http://localhost:8098/ws';
    console.log('🔌 Connexion WebSocket à:', wsUrl);

    this.client = new Client({
      webSocketFactory: () => new SockJS(wsUrl) as any,
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      debug: (str) => {
        console.log('🐛 WebSocket debug:', str);
      }
    });

    this.client.onConnect = () => {
      console.log('✅ WebSocket connecté!');
      this.subscription?.unsubscribe();
      this.subscription = this.client?.subscribe('/topic/doctor-notifications', (msg: IMessage) => {
        console.log('📨 Message WebSocket reçu:', msg.body);
        try {
          const payload = JSON.parse(msg.body);
          this.notificationsSubject.next(payload);
        } catch {
          this.notificationsSubject.next({ message: msg.body });
        }
      });
      console.log('👂 Abonné au topic: /topic/doctor-notifications');
    };

    this.client.onStompError = (frame) => {
      console.error('❌ Erreur STOMP:', frame);
    };

    this.client.onWebSocketError = (event) => {
      console.error('❌ Erreur WebSocket:', event);
    };

    this.client.activate();
  }

  disconnect(): void {
    this.subscription?.unsubscribe();
    this.subscription = undefined;

    if (this.client?.active) {
      this.client.deactivate();
    }
    this.client = undefined;
  }
}
