import { ChangeDetectorRef, Component, computed, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { UserService } from '../../../../../service/user-service';
import { isPlatformBrowser } from '@angular/common';
import { DialogBox } from '../../../../../utils/dialog-box/dialog-box';
import { NameModel } from '../../../../../model/requestModel/NameModel';
import { CommonFun } from '../../../../../utils/helper/CommonFun';
import { Router } from '@angular/router';
import { allRoutes } from '../../../../../utils/allRoutes/allRoutes';
import { IdModel } from '../../../../../model/requestModel/IdModel';
import { FormControl, FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'app-find-friend',
    imports: [DialogBox, FormsModule],
    templateUrl: './find-friend.html',
    styleUrl: './find-friend.scss',
})
export class FindFriend implements OnInit {

    pageTitle = "";
    pageStatusName = "";
    private platformId = inject(PLATFORM_ID);
    private userService = inject(UserService);
    // userList = signal<any[]>([]);
    showModal = false;
    selectedUserId: number | null = null;
    modalTitle = '';
    modalMessage = '';
    status = '';

    // Search input ki value hold karne ke liye signal
    searchTerm = signal<string>('');

    // Pagination & Scroll Trackers
    currentPage = signal<number>(0);
    readonly pageSize = 10;
    hasMore = signal<boolean>(true);
    isLoading = signal<boolean>(false);

    searchControl = new FormControl('');
    private destroy$ = new Subject<void>();

    readonly friendList = this.userService.friendList;

    readonly userList = this.userService.allUserList;

    readonly friendRequestList = this.userService.getFriendRequest;

    readonly displayedUsers = computed(() => {

        if (this.pageStatusName === allRoutes.friendLists) {
            const term = this.searchTerm().toLowerCase().trim();
            if (!term) {
                return this.friendList();
            }
            return this.friendList().filter(item =>
                item.fullName?.toLowerCase().includes(term)
            );
        }

        else if (this.pageStatusName === allRoutes.findFriend) {
            const term = this.searchTerm().toLowerCase().trim();
            if (!term) {
                return this.userList();
            }
            return this.userList().filter(item =>
                item.fullName?.toLowerCase().includes(term)
            );
        }

        else if (this.pageStatusName === allRoutes.friendRequestNotify) {
            const term = this.searchTerm().toLowerCase().trim();
            if (!term) {
                return this.friendRequestList();
            }
            return this.friendRequestList().filter(item =>
                item.fullName?.toLowerCase().includes(term)
            );
        }

        return null;
    });

    constructor(private commonFunctionObject: CommonFun, private router: Router, private cdr: ChangeDetectorRef) {

    }


    ngOnInit(): void {
        console.log('----find-friend user module running--------ngOnInit------');

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

        // 1. Initial Page 1 Load
        this.loadUsers(0, true);

        // 2. Search Debounce
        this.searchControl.valueChanges.pipe(debounceTime(350), distinctUntilChanged(), takeUntil(this.destroy$)).subscribe(() => {
            this.currentPage.set(0);
            this.hasMore.set(true);
            this.loadUsers(0, true);
        });
    }

    // Backend Paginated Call
    loadUsers(page: number, isReset: boolean = false): void {

        if (this.isLoading() || (!this.hasMore() && !isReset)) return;

        this.isLoading.set(true);
        const searchVal = this.searchControl.value || '';

        if (isPlatformBrowser(this.platformId)) {

            if (allRoutes.friendLists == this.pageStatusName) {

                this.userService.getFriendListPaginated(page, this.pageSize, searchVal).subscribe({
                    next: (res: any) => {
                        const responseData = res?.data ?? [];

                        // Agar response me aane wala data 10 se kam hai toh aage aur data nahi hai
                        if (responseData.length < this.pageSize) {
                            this.hasMore.set(false);
                        }

                        this.currentPage.set(page);
                        this.isLoading.set(false);
                        this.cdr.detectChanges();
                    },
                    error: () => {
                        this.isLoading.set(false);
                    }
                });
            } else if (allRoutes.findFriend == this.pageStatusName) {

                this.userService.getAllUserListPaginated(page, this.pageSize, searchVal).subscribe({
                    next: (res: any) => {
                        const responseData = res?.data ?? [];

                        // Agar response me aane wala data 10 se kam hai toh aage aur data nahi hai
                        if (responseData.length < this.pageSize) {
                            this.hasMore.set(false);
                        }

                        this.currentPage.set(page);
                        this.isLoading.set(false);
                        this.cdr.detectChanges();
                    },
                    error: () => {
                        this.isLoading.set(false);
                    }
                });

            } else if (allRoutes.friendRequestNotify == this.pageStatusName) {

                this.userService.getFriendRequestPaginated(page, this.pageSize, searchVal).subscribe({
                    next: (res: any) => {
                        const responseData = res?.data ?? [];

                        // Agar response me aane wala data 10 se kam hai toh aage aur data nahi hai
                        if (responseData.length < this.pageSize) {
                            this.hasMore.set(false);
                        }

                        this.currentPage.set(page);
                        this.isLoading.set(false);
                        this.cdr.detectChanges();
                    },
                    error: () => {
                        this.isLoading.set(false);
                    }
                });
            }
        }
    }

    // Mouse Scroll / Drag trigger
    onUserListScroll(event: Event): void {
        const target = event.target as HTMLElement;
        const isAtBottom = target.scrollHeight - target.scrollTop <= target.clientHeight + 30;

        if (isAtBottom && !this.isLoading() && this.hasMore()) {
            this.loadUsers(this.currentPage() + 1, false);
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
