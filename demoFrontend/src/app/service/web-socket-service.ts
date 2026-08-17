import { Service } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import { BehaviorSubject } from 'rxjs';
import { allRoutes } from '../utils/allRoutes/allRoutes';

// export interface ChatMessage {

//     sender: string;
//     receiver: string;
//     content: string;
//     timestamp?: string;
// }

@Service()
export class WebSocketService {

    stompClient: Client;

    // Subject to manage the stream of incoming messages
    private messageSubject = new BehaviorSubject<any>(null);
    public messages$ = this.messageSubject.asObservable();  // Observable for components to subscribe to messages

    // Subject to track the connection status (connected/disconnected)
    private connectionSubject = new BehaviorSubject<boolean>(false);
    public connectionStatus$ = this.connectionSubject.asObservable();

    constructor() {

        this.disconnect();

        this.stompClient = new Client({
            brokerURL: allRoutes.backendWebSocketUrl,
            reconnectDelay: 5000,
        });
    }

    connect(targetUserId: number) {

        // On successful connection
        this.stompClient.onConnect = (frame) => {
            console.log('Connected to WebSocket server');
            this.connectionSubject.next(true);  // Notify that the connection is successful

            // Subscribe to the '/user/private' topic to receive public messages
            this.stompClient?.subscribe(allRoutes.userPrivateBackendUrl, (message: Message) => {
                this.messageSubject.next(JSON.parse(message.body));
            });

            // Send a "JOIN" message to notify the server that a user has joined
            this.stompClient?.publish({
                destination: allRoutes.oneToOneAddUser,
                body: JSON.stringify({ recipient: targetUserId, type: 'JOIN' })
            });
        };

        this.stompClient.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };

        this.stompClient?.activate();
    }

    sendMessage(recipientUserId: number, content: string) {

        if (this.stompClient && this.stompClient.connected) {
            // Create a chat message object
            const chatMessage = { recipient: recipientUserId, content: content, dataTime: new Date(), type: 'CHAT' };

            // Log the message being sent and the sender
            // console.log(`Message sent by ${username}: ${content}`);

            // Publish (send) the message to the '/app/chat.sendMessage' destination
            this.stompClient.publish({
                destination: allRoutes.oneToOneSendMessage,
                body: JSON.stringify(chatMessage)
            });
        } else {
            // Log an error if the WebSocket connection is not active
            console.error('WebSocket is not connected. Unable to send message.');
        }

    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.deactivate();
        }
    }

}
