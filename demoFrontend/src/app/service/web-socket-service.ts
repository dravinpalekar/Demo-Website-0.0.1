import { inject, PLATFORM_ID, Service } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import { BehaviorSubject, Subject } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthenticationService } from './authentication-service';
import { allRoutes } from '../utils/allRoutes/allRoutes';

// export interface ChatMessage {

//     sender: string;
//     receiver: string;
//     content: string;
//     timestamp?: string;
// }

@Service()
export class WebSocketService {

    private jwtToken?: string;
    private headersLocal;
    private http = inject(HttpClient);
    private authenticationServiceObject = inject(AuthenticationService);

    constructor(){
        this.jwtToken = this.authenticationServiceObject.currentUserValue?.token;
        this.headersLocal = {'Authorization': `Bearer ${this.jwtToken}`};
    }

    stompClient: Client | null = null;  // STOMP client instance to handle WebSocket connection

    // Subject to manage the stream of incoming messages
    private messageSubject = new BehaviorSubject<any>(null);
    public messages$ = this.messageSubject.asObservable();  // Observable for components to subscribe to messages

    // Subject to track the connection status (connected/disconnected)
    private connectionSubject = new BehaviorSubject<boolean>(false);
    public connectionStatus$ = this.connectionSubject.asObservable();

    async connect(username: string | undefined) {

        this.disconnect();

        this.stompClient = new Client({
                brokerURL: allRoutes.backendWebSocketUrl,
                reconnectDelay: 5000,
                connectHeaders: {
                username: username!
            }
        });

        // On successful connection
        this.stompClient.onConnect = (frame) => {
            console.log('Connected to WebSocket server');
            this.connectionSubject.next(true);  // Notify that the connection is successful

            // Subscribe to the '/topic/public' topic to receive public messages
            this.stompClient?.subscribe(allRoutes.userPrivateBackendUrl, (message: Message) => {
                this.messageSubject.next(JSON.parse(message.body));  // Pass the message to subscribers
            });

            // Send a "JOIN" message to notify the server that a user has joined
            this.stompClient?.publish({
                destination: allRoutes.oneToOneAddUser,  // Server endpoint for adding users
                body: JSON.stringify({ sender: username, type: 'JOIN' })  // Send username and join event
            });
        };

        this.stompClient.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);  // Log the error message
            console.error('Additional details: ' + frame.body);  // Log additional error details
        };

        this.stompClient?.activate();
    }

    sendMessage(username: string | undefined, recipientUsername: string, content: string) {

        if (this.stompClient && this.stompClient.connected) {
            // Create a chat message object
            const chatMessage = { sender: username,recipient: recipientUsername, content: content, type: 'CHAT' };

            // Log the message being sent and the sender
            console.log(`Message sent by ${username}: ${content}`);

            // Publish (send) the message to the '/app/chat.sendMessage' destination
            this.stompClient.publish({
                destination: allRoutes.oneToOneSendMessage,
                body: JSON.stringify(chatMessage)  // Convert the message to JSON and send
            });
        } else {
            // Log an error if the WebSocket connection is not active
            console.error('WebSocket is not connected. Unable to send message.');
        }

    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.deactivate();  // Deactivate the WebSocket connection
        }
    }

    // send(message: ChatMessage): void {

    //     this.stompClient.publish({
    //         destination: '/app/sendMessage/'+"2232",
    //         body: JSON.stringify(message)
    //     });
    // }

    // createRoom(id:string){
    //     return this.http.post("http://localhost:8081/api/v1/rooms", {"roomId":id});
    // }

    // joinRoom(id:string){
    //     // return this.http.get("http://localhost:8081/api/v1/rooms/"+id);
    //     this.stompClient.onConnect = () => {

    //         const subscription = this.stompClient.subscribe(
    //             `/topic/room/${id}`,
    //             (message) => {

    //                 console.log("Message Received :", message.body);

    //                 this.messageSubject.next(
    //                     JSON.parse(message.body)
    //                 );

    //             }
    //         );

    //         console.log("Subscribed Successfully");
    //         console.log(subscription);

    //     };
    // }

    // disconnect(): void {
    //     this.stompClient.deactivate();
    // }

}
