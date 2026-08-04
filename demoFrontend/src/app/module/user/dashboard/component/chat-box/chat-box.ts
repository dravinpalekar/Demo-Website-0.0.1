import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { WebSocketService } from '../../../../../service/web-socket-service';
import { FormsModule } from '@angular/forms';
import { AuthenticationService } from '../../../../../service/authentication-service';

@Component({
  selector: 'app-chat-box',
  imports: [FormsModule],
  templateUrl: './chat-box.html',
  styleUrl: './chat-box.scss',
})
export class ChatBox implements OnInit, OnDestroy {

  title = 'frontend';
  anotherUsername: string = '';  // Stores the username entered by the user
  message: string = '';  // Stores the message being typed by the user
  messages: any[] = [];  // Stores all the chat messages
  isConnected = false;  // Tracks whether the user is connected to the WebSocket
  connectingMessage = 'Connecting...';  // Message to show while connecting
  smsStatus = false;

  currentUserName: string = "";

  constructor(private socketService: WebSocketService, private authenticationService: AuthenticationService,private cdr: ChangeDetectorRef) { 

    this.currentUserName = this.authenticationService.currentUserValue.Subject!;
  }

  ngOnInit(): void {

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

  connect(username: string): void {
    console.log('Attempting to connect to WebSocket at http://localhost:8080/ws with username:', this.currentUserName);
    this.socketService.connect(this.currentUserName);
    this.anotherUsername = username;
  }

  sendMessage(event:Event) {
    event.preventDefault();
     const chatMessage = { sender: this.currentUserName,recipient: this.anotherUsername, content: this.message, type: 'CHAT' };
      this.messages.push(chatMessage);
    if (this.message) {
      this.socketService.sendMessage(this.currentUserName, this.anotherUsername, this.message);  // Send the message via WebSocket service
      this.message = '';  // Clear the message input after sending
      //  this.smsStatus = true;
    }
  }

  ngOnDestroy(): void {
    this.socketService.disconnect();
  }





}
