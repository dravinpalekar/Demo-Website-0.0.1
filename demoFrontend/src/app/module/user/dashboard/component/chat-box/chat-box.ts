import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { WebSocketService } from '../../../../../service/web-socket-service';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { DatePipe, isPlatformBrowser, NgOptimizedImage } from '@angular/common';
import { UserService } from '../../../../../service/user-service';

@Component({
  selector: 'app-chat-box',
  imports: [FormsModule, NgOptimizedImage, DatePipe],
  templateUrl: './chat-box.html',
  styleUrl: './chat-box.scss',
})
export class ChatBox implements OnInit, OnDestroy {

  private cookieService = inject(CookieService);
  private platformId = inject(PLATFORM_ID);

  title = 'frontend';
  anotherUsername: string = '';  // Stores the username entered by the user
  message: string = '';  // Stores the message being typed by the user
  messages: any[] = [];  // Stores all the chat messages
  isConnected = false;  // Tracks whether the user is connected to the WebSocket
  connectingMessage = 'Connecting...';  // Message to show while connecting
  showMessagingTextBox = false;
  anotherPhotoUrl: any;
  currentPhotoUrl: any;

  userList = signal<any[]>([]);
  currentUserName: string = "";

  constructor(private socketService: WebSocketService, private userService: UserService, private cdr: ChangeDetectorRef) {

    if (this.cookieService.get("isLoggedIn")) {
      this.currentUserName = JSON.parse(this.cookieService.get("userSession")).userName;
    }

  }

  ngOnInit(): void {

    if (isPlatformBrowser(this.platformId)) {
      this.userService.getFriendList().subscribe({
        next: (res) => {
          this.userList.set(JSON.parse(JSON.stringify(res)).data);
        }
      })
    }


    // Subscribe to messages observable to receive messages from the WebSocket service
    this.socketService.messages$.subscribe(message => {
      if (message) {
        // Log and add the received message to the array of messages
        console.log(`Message received from ${message.sender}: ${message.content}`);

        //  const chatMessage = { sender: message.sender,recipient: message.recipientUsername, content: message.content, type: 'CHAT' };
        this.messages.push(message);
        this.cdr.detectChanges();
        // console.log(this.messages);
      }
    });

    // Subscribe to connection status observable to monitor connection status
    this.socketService.connectionStatus$.subscribe(connected => {
      this.isConnected = connected;  // Update the connection status
      if (connected) {
        this.connectingMessage = '';  // Clear the connecting message once connected
        console.log('WebSocket connection established');
      }
    });
  }

  connect(username: string, photoData: string): void {
    this.showMessagingTextBox = true;
    console.log('Attempting to connect to WebSocket at http://localhost:8080/ws with username:', this.currentUserName);
    this.socketService.connect(this.currentUserName);
    
    this.anotherUsername = username;
    this.anotherPhotoUrl = photoData;
    let imgElement = document.querySelector('.user-image') as HTMLImageElement;
    this.currentPhotoUrl = imgElement.src;
  }

  sendMessage(event: Event) {
    event.preventDefault();
    const chatMessage = { sender: this.currentUserName, recipient: this.anotherUsername, content: this.message, dataTime: new Date(), type: 'CHAT' };
    this.messages.push(chatMessage);
    if (this.message) {
      this.socketService.sendMessage(this.currentUserName, this.anotherUsername, this.message);  // Send the message via WebSocket service
      this.message = '';  // Clear the message input after sending
    }
  }

  ngOnDestroy(): void {
    this.socketService.disconnect();
  }

}
