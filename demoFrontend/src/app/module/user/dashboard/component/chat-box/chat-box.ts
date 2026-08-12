import { ChangeDetectorRef, Component, ElementRef, inject, OnDestroy, OnInit, PLATFORM_ID, signal, ViewChild } from '@angular/core';
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

    messageTextBoxArea: string = '';
    messages: any[] = [];
    isConnected = false;
    connectingMessage = 'Connecting...';

    showMessagingTextBox = false;

    anotherPhotoUrl: any;
    currentPhotoUrl: any;

    friendList = signal<any[]>([]);
    currentUserName: string = "";
    anotherUsername: string = '';

    @ViewChild('messagesContainer') private messagesContainer!: ElementRef<HTMLDivElement>;

    constructor(private socketService: WebSocketService, private userService: UserService, private cdr: ChangeDetectorRef) {

        if (this.cookieService.get("isLoggedIn")) {
            this.currentUserName = JSON.parse(this.cookieService.get("userSession")).userName;
        }
    }

    ngOnInit(): void {

        if (isPlatformBrowser(this.platformId)) {
            this.userService.getFriendList().subscribe({
                next: (res) => {
                    this.friendList.set(JSON.parse(JSON.stringify(res)).data);
                }
            })
        }

        // Subscribe to messages observable to receive messages from the WebSocket service
        this.socketService.messages$.subscribe(message => {

            if (message) {

                 if (message.type === 'LEAVE') {
                     console.log(`${message.sender} has leave the chat room.`);
                      this.messages.push(message);
                 }

                if (message.type === 'JOIN') {
                    // console.log(`${message.sender} is now online/connected.`);

                    if (message.sender === this.anotherUsername) {
                        console.log(`${message.sender} has joined the chat room.`);
                    }

                    // if (message.sender === this.currentUserName) {
                    //     console.log(`${message.recipient} has joined the chat room.`);
                    // }

                }

                if (message.type === 'CHAT') {
                   // Log and add the received message to the array of messages
                    console.log(`Message received from ${message.sender}: ${message.content}`);

                    //  const chatMessage = { sender: message.sender,recipient: message.recipientUsername, content: message.content, type: 'CHAT' };
                    this.messages.push(message);
                }

                    this.cdr.detectChanges();
                    // console.log(this.messages);
                    this.scrollToBottom();
                
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

    connect(targetUserName: string, targetPhotoData: string): void {

        this.showMessagingTextBox = true;
        console.log('Attempting to connect to WebSocket at http://localhost:8080/ws with username:', this.currentUserName);
        this.socketService.connect(this.currentUserName, targetUserName);

        this.anotherUsername = targetUserName;
        this.anotherPhotoUrl = targetPhotoData;
        let imgElement = document.querySelector('.user-image') as HTMLImageElement;
        this.currentPhotoUrl = imgElement.src;
    }

    sendMessage(event: Event) {

        event.preventDefault();
        const chatMessage = { sender: this.currentUserName, recipient: this.anotherUsername, content: this.messageTextBoxArea, dataTime: new Date(), type: 'CHAT' };
        this.messages.push(chatMessage);
        if (this.messageTextBoxArea) {
            this.socketService.sendMessage(this.currentUserName, this.anotherUsername, this.messageTextBoxArea);  // Send the message via WebSocket service
            this.messageTextBoxArea = '';  // Clear the message input after sending
            this.scrollToBottom();
        }
    }

    ngOnDestroy(): void {
        this.socketService.disconnect();
    }

    private scrollToBottom(): void {

        if (!isPlatformBrowser(this.platformId)) {
            return;
        }

        setTimeout(() => {
            const element = this.messagesContainer?.nativeElement;

            if (element) {
                element.scrollTop = element.scrollHeight;
            }
        });
    }

}
