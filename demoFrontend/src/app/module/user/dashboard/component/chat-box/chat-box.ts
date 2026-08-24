import { ChangeDetectorRef, Component, CUSTOM_ELEMENTS_SCHEMA, ElementRef, HostListener, inject, OnDestroy, OnInit, PLATFORM_ID, signal, ViewChild } from '@angular/core';
import { WebSocketService } from '../../../../../service/web-socket-service';
import { FormControl, FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { DatePipe, isPlatformBrowser, NgOptimizedImage } from '@angular/common';
import { UserService } from '../../../../../service/user-service';
import { CommonFun } from '../../../../../utils/helper/CommonFun';
import { FileValidation } from '../../../../../utils/formValidation/FileValidation';
import { IdModel } from '../../../../../model/requestModel/IdModel';

@Component({
    selector: 'app-chat-box',
    imports: [FormsModule, NgOptimizedImage, DatePipe,],
    templateUrl: './chat-box.html',
    styleUrl: './chat-box.scss',
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
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
    currentUserId: string = "";
    anotherUserId!: number;

    showEmojiPicker = false;
    isUploading = false;

    @ViewChild('messagesContainer') private messagesContainer!: ElementRef<HTMLDivElement>;
    @ViewChild('fileInput') private fileInput!: ElementRef<HTMLInputElement>;

    constructor(private socketService: WebSocketService, private userService: UserService, private cdr: ChangeDetectorRef, private commonFunctionObject: CommonFun) {

        if (this.cookieService.get("isLoggedIn")) {
            this.currentUserId = JSON.parse(this.cookieService.get("userSession")).id;
        }
    }

    async ngOnInit(): Promise<void> {

        if (isPlatformBrowser(this.platformId)) {

            await import('emoji-picker-element');

            this.userService.getFriendList().subscribe({
                next: (res) => {
                    this.friendList.set(JSON.parse(JSON.stringify(res)).data);
                }
            })
        }

        this.socketService.messages$.subscribe(message => {

            if (message) {

                if (message.type === 'CONNECTED') {
                    // console.log('Both users are connected successfully');
                    this.messages.push(message);
                }

                if (message.type === 'LEAVE') {
                    //  console.log(`${message.sender} has leave the chat room.`);
                    this.messages.push(message);
                }

                if (message.type === 'JOIN') {

                    if (message.sender === this.anotherUserId) {
                        // console.log(`${message.sender} has joined the chat room.`);
                    }
                }

                if (message.type === 'CHAT' || message.type === 'IMAGE') {
                    // console.log(`Message received from ${message.sender}: ${message.content}`);
                    this.messages.push(message);
                }

                this.cdr.detectChanges();
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

    connect(targetUserId: number, targetPhotoData: string): void {

        let roomId = "";

        if (isPlatformBrowser(this.platformId)) {
            this.userService.createRoomOrGetRoom(new IdModel(targetUserId)).subscribe({
                next: (res) => {
                    roomId = JSON.parse(JSON.stringify(res)).data;

                    this.showMessagingTextBox = false;
                    this.messages = [];
                    // this.ngOnDestroy();
                    
                    this.showMessagingTextBox = true;
                    this.cdr.detectChanges();
                    // console.log('Attempting to connect to WebSocket at http://localhost:8080/ws with username:', this.currentUserName);
                    this.socketService.connect(targetUserId, roomId);
            
                    this.anotherUserId = targetUserId;
                    this.anotherPhotoUrl = targetPhotoData;
                    let imgElement = document.querySelector('.user-image') as HTMLImageElement;
                    this.currentPhotoUrl = imgElement.src;
                },
                error: (e) => {

                    // if (e.status == 400) {
                    //     this.commonFunctionObject.openSnackBar(e.error.detail, 'danger');
                    // }
                }
            });
        }

        // this.showMessagingTextBox = false;
        // this.messages = [];
        // this.ngOnDestroy();
        // this.showMessagingTextBox = true;
        // // console.log('Attempting to connect to WebSocket at http://localhost:8080/ws with username:', this.currentUserName);
        // this.socketService.connect(targetUserId, roomId);

        // this.anotherUserId = targetUserId;
        // this.anotherPhotoUrl = targetPhotoData;
        // let imgElement = document.querySelector('.user-image') as HTMLImageElement;
        // this.currentPhotoUrl = imgElement.src;
    }

    sendMessage(event: Event) {

        if (this.messageTextBoxArea.trim() !== '') {

            event.preventDefault();
            const chatMessage = { sender: this.currentUserId, recipient: this.anotherUserId, content: this.messageTextBoxArea, dataTime: new Date(), type: 'CHAT' };
            this.messages.push(chatMessage);

            this.socketService.sendMessage(this.anotherUserId, this.messageTextBoxArea, "CHAT");  // Send the message via WebSocket service
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

    onEmojiClick(event: any): void {

        this.messageTextBoxArea += event.detail.unicode;
    }

    toggleEmojiPicker(event: MouseEvent): void {

        event.preventDefault();
        event.stopPropagation();

        this.showEmojiPicker = !this.showEmojiPicker;
    }

    @HostListener('document:click')
    onDocumentClick(): void {
        this.showEmojiPicker = false;
    }


    onImageSelected(event: Event): void {

        const input = event.target as HTMLInputElement;
        if (input.files && input.files[0]) {
            const file = input.files[0];

            if (this.checkFileValidation(file))
                return;

            this.isUploading = true;
            this.scrollToBottom();
            const requestData = new FormData();
            requestData.append('file', file);

            this.userService.uploadChatImage(requestData).subscribe({
                next: (res) => {
                    const url = JSON.parse(JSON.stringify(res)).data;

                    const chatMessage = { sender: this.currentUserId, recipient: String(this.anotherUserId), content: url, dataTime: new Date(), type: 'IMAGE' };

                    this.messages.push(chatMessage);
                    this.isUploading = false;

                    this.socketService.sendMessage(this.anotherUserId, url, "IMAGE");           // Send the message via WebSocket service
                    this.cdr.detectChanges();
                    this.scrollToBottom();
                },
                error: (e) => {
                    this.isUploading = false;

                    if (e.status == 400) {
                        this.commonFunctionObject.openSnackBar(e.error.detail, 'danger');
                    }
                }
            });
        }
        // this.scrollToBottom();
    }

    private checkFileValidation(file: File) {

        let fileControl = new FormControl<File | null>(null, [
            FileValidation(['image/jpeg', 'image/jpg', 'image/jpe', 'image/png'], 1)
        ]);

        let fileErrorMessage: string | null = null;

        fileControl.setValue(file);
        fileControl.updateValueAndValidity();

        // Step 2: Check karein agar validator ne error diya hai
        if (fileControl.invalid) {
            const errors = fileControl.errors;

            if (errors?.['invalidFileType']) {
                fileErrorMessage = 'This file type is not allowed. Please select a valid image file.(jpeg, jpg, jpe, png)';
            } else if (errors?.['maxFileSize']) {
                fileErrorMessage = `File size too big. Please compress or choose a smaller file (Max: 1 MB).`;
            }

            fileControl.reset();
            // if (this.fileInput) {
            //     this.fileInput.nativeElement.value = '';
            // }
            this.commonFunctionObject.openSnackBar(fileErrorMessage, 'danger');
            return true;
        }
        return false;
    }

}
