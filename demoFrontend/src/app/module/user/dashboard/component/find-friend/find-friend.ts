import { Component, computed, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { UserService } from '../../../../../service/user-service';
import { isPlatformBrowser } from '@angular/common';
import { DialogBox } from '../../../../../utils/dialog-box/dialog-box';
import { NameModel } from '../../../../../model/requestModel/NameModel';
import { CommonFun } from '../../../../../utils/helper/CommonFun';
import { Router } from '@angular/router';
import { allRoutes } from '../../../../../utils/allRoutes/allRoutes';
import { IdModel } from '../../../../../model/requestModel/IdModel';

@Component({
    selector: 'app-find-friend',
    imports: [DialogBox],
    templateUrl: './find-friend.html',
    styleUrl: './find-friend.scss',
})
export class FindFriend implements OnInit {

    pageTitle = "";
    pageStatusName = "";
    private platformId = inject(PLATFORM_ID);
     private userService = inject(UserService);
    userList = signal<any[]>([]);
    showModal = false;
    selectedUserId: number | null = null;
    modalTitle = '';
    modalMessage = '';
    status = '';

    readonly friendList = this.userService.friendList;

    readonly displayedUsers = computed(() => {

        if (this.pageStatusName === allRoutes.friendLists) {
            return this.friendList();
        }

        return this.userList();

    });

    constructor( private commonFunctionObject: CommonFun, private router: Router) {
        
    }


    ngOnInit(): void {
        console.log('----find-friend user module running--------ngOnInit------');

        // alert(this.router.url);

        if (this.router.url.includes(allRoutes.findFriend)) {
            this.pageTitle = "Find friend";
            this.pageStatusName = allRoutes.findFriend;
        } else if (this.router.url.includes(allRoutes.friendLists)) {
            this.pageTitle = "Friend List";
            this.pageStatusName = allRoutes.friendLists;
        } else if (this.router.url.includes(allRoutes.friendRequestNotify)) {
            this.pageTitle = "Friend Request Notification";
            this.pageStatusName = allRoutes.friendRequestNotify;
        }

        if (isPlatformBrowser(this.platformId)) {
            if (allRoutes.findFriend == this.pageStatusName) {

                this.userService.getAllUserList().subscribe({
                    next: (res) => {
                        this.userList.set(JSON.parse(JSON.stringify(res)).data);
                    }
                })

            } else if (allRoutes.friendLists == this.pageStatusName) {

                this.userService.getFriendList().subscribe();

            } else if (allRoutes.friendRequestNotify == this.pageStatusName) {

                this.userService.getFriendRequestList().subscribe({
                    next: (res) => {
                        this.userList.set(JSON.parse(JSON.stringify(res)).data);
                    }
                })

            }
        }

    }

    serachFriend() {
        alert("search button");
    }

    cancelRequest(userId: number) {

        this.selectedUserId = userId;
        this.showModal = true;
        this.status = "cancel"

        this.modalTitle = 'Cancel Request';
        this.modalMessage = 'Are you sure you want to cancel the request?';
    }

    sendRequest(userId: number) {

        this.selectedUserId = userId;
        this.showModal = true;

        if (allRoutes.friendRequestNotify == this.pageStatusName) {
            this.modalTitle = 'Accept Request';
            this.modalMessage = 'Are you sure you want to accept the request?';
        }
        else if (allRoutes.friendLists == this.pageStatusName) {
            this.modalTitle = 'Unfriend Request';
            this.modalMessage = 'Are you sure you want to unfriend the request?';
        }
        else if (allRoutes.findFriend == this.pageStatusName) {
            this.modalTitle = 'Send Request';
            this.modalMessage = 'Are you sure you want to send the request?';
        }
    }

    closeModal() {
        this.showModal = false;
        this.selectedUserId = null;
    }

    onConfirm(result: boolean) {

        this.showModal = false;

        if (result && this.selectedUserId !== null) {

            if (allRoutes.friendRequestNotify == this.pageStatusName) {
                if (this.status == "cancel") {
                    this.cancelFriendRequest(this.selectedUserId);
                }
                else {
                    this.acceptFriendRequest(this.selectedUserId);
                }
            }
            else if (allRoutes.findFriend == this.pageStatusName) {
                this.sendFriendRequest(this.selectedUserId);
            }

        }

        this.selectedUserId = null;
        this.status = '';
    }

    private cancelFriendRequest(userId: number) {

        const requestData: IdModel = new IdModel(userId);

        if (isPlatformBrowser(this.platformId)) {
            this.userService.cancelFriendRequest(requestData).subscribe({
                next: (res) => {
                    this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
                },
                error: (e) => {
                    if (e.status == 404 || e.status == 409) {
                        this.commonFunctionObject.openSnackBar(e.error.message, 'danger');
                    }
                }
            })
        }

    }

    private acceptFriendRequest(userId: number) {
        const requestData: IdModel = new IdModel(userId);

        if (isPlatformBrowser(this.platformId)) {
            this.userService.acceptFriendRequest(requestData).subscribe({
                next: (res) => {
                    this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
                },
                error: (e) => {
                    if (e.status == 404 || e.status == 409) {
                        this.commonFunctionObject.openSnackBar(e.error.message, 'danger');
                    }
                }
            })
        }
    }

    private sendFriendRequest(userId: number) {
        const requestData: IdModel = new IdModel(userId);

        if (isPlatformBrowser(this.platformId)) {
            this.userService.sendFriendRequest(requestData).subscribe({
                next: (res) => {
                    this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
                },
                error: (e) => {
                    if (e.status == 404 || e.status == 409) {
                        this.commonFunctionObject.openSnackBar(e.error.message, 'danger');
                    }
                }
            })
        }
    }

}
