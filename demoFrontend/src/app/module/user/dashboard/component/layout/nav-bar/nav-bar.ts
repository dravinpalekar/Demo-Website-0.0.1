import { ChangeDetectorRef, Component, computed, inject, Input, PLATFORM_ID, Renderer2, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthenticationService } from '../../../../../../service/authentication-service';
import { SuperAdminService } from '../../../../../../service/superAdmin/super-admin-service';
import { allRoutes } from '../../../../../../utils/allRoutes/allRoutes';
import { CommonFun } from '../../../../../../utils/helper/CommonFun';
import { CookieService } from 'ngx-cookie-service';
import { isPlatformBrowser, NgOptimizedImage } from '@angular/common';
import { UserService } from '../../../../../../service/user-service';
import { FormControl, FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'app-nav-bar',
    imports: [RouterModule, NgOptimizedImage, FormsModule],
    templateUrl: './nav-bar.html',
    styleUrl: './nav-bar.scss',
})
export class NavBar {

    @Input() navBarPageTitle: string = '';
    displayEmail: string | undefined;
    // profileImage: any;
    private cookieService = inject(CookieService);
    private userService = inject(UserService);
    private platformId = inject(PLATFORM_ID);
    profileImage = signal<string>('');

    // Search input ki value hold karne ke liye signal
    searchTerm = signal<string>('');

    public readonly friendList = this.userService.friendList;

    // Pagination & Scroll Trackers
    currentPage = signal<number>(0);
    readonly pageSize = 10;
    hasMore = signal<boolean>(true);
    isLoading = signal<boolean>(false);

    searchControl = new FormControl('');
    private destroy$ = new Subject<void>();

    // Automatically filter hone wala computed signal
    filteredFriendList = computed(() => {

        const term = this.searchTerm().toLowerCase().trim();
        if (!term) {
            return this.friendList();
        }
        return this.friendList().filter(item =>
            item.fullName?.toLowerCase().includes(term)
        );
    });

    constructor(private authenticationServiceObject: AuthenticationService, private router: Router, private SuperAdminServiceObject: SuperAdminService, private renderer: Renderer2, private commonFunctionObject: CommonFun, private cdr: ChangeDetectorRef) {
        // this.commonFunctionObject.loadStyle( this.renderer, 'assets/superAdminModule/simplebar/simplebar.css' );
        //  this.commonFunctionObject.loadScript( this.renderer, 'assets/superAdminModule/simplebar/simplebar.min.js' );
        if (this.cookieService.get("isLoggedIn")) {
            this.displayEmail = JSON.parse(this.cookieService.get("userSession")).userName;
        }
    }

    ngOnInit(): void {
        console.log("----nav-bar-Super-Admin module running--------ngOnInit------");

        if (isPlatformBrowser(this.platformId)) {
            this.SuperAdminServiceObject.getMyImage().subscribe({
                next: (res) => { //console.log(JSON.parse(JSON.stringify(res)).data);
                    let responseData = JSON.parse(JSON.stringify(res));
                    // if (responseData.image == null || responseData.image == undefined || responseData.image == '')
                    //   if (responseData.gender == "MALE")
                    //     this.profileImage = 'assets/superAdminModule/images/user/default-image-men.jpg';
                    //   else
                    //     this.profileImage = 'assets/superAdminModule/images/user/default-image-women.jpg';
                    // else
                    this.profileImage.set(responseData.image);
                    // this.profileImage = responseData.image;

                    this.cdr.detectChanges();

                },
                error: (e) => {// console.log(e);
                },
            });

            // 1. Initial Page 1 Load
            this.loadUsers(0, true);

            // 2. Search Debounce
            this.searchControl.valueChanges
                .pipe(
                    debounceTime(350),
                    distinctUntilChanged(),
                    takeUntil(this.destroy$)
                )
                .subscribe(() => {
                    this.currentPage.set(0);
                    this.hasMore.set(true);
                    this.loadUsers(0, true);
                });

        }
    }

    // Backend Paginated Call
    loadUsers(page: number, isReset: boolean = false): void {
        if (this.isLoading() || (!this.hasMore() && !isReset)) return;

        this.isLoading.set(true);
        const searchVal = this.searchControl.value || '';

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
    }

    // Mouse Scroll / Drag trigger
    onUserListScroll(event: Event): void {
        const target = event.target as HTMLElement;
        const isAtBottom = target.scrollHeight - target.scrollTop <= target.clientHeight + 30;

        if (isAtBottom && !this.isLoading() && this.hasMore()) {
            this.loadUsers(this.currentPage() + 1, false);
        }
    }

    logout() {
        this.authenticationServiceObject.logout().subscribe();
        this.router.navigate([allRoutes.login]);
        // Implement your logout logic here
        // For example, you might want to clear user data and redirect to the login page
    }
}
