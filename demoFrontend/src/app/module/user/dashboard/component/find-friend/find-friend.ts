import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { UserService } from '../../../../../service/user-service';
import { FormsModule } from '@angular/forms';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-find-friend',
  imports: [],
  templateUrl: './find-friend.html',
  styleUrl: './find-friend.scss',
})
export class FindFriend implements OnInit {

  private platformId = inject(PLATFORM_ID);
  userList = signal<any[]>([]);

  constructor(private userService: UserService) { 

     
  }


  ngOnInit(): void {
    console.log('----find-friend user module running--------ngOnInit------');

    //  if (isPlatformBrowser(this.platformId)) {
    //   this.userService.getAllUserList().subscribe({
    //     next: (res) => {
    //      this.userList.set( JSON.parse(JSON.stringify(res)).data);
    //     }
    //   })
    //   }
  }

}
