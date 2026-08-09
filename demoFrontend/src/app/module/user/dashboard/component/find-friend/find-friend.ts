import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { UserService } from '../../../../../service/user-service';
import { isPlatformBrowser } from '@angular/common';
import { DialogBox } from '../../../../../utils/dialog-box/dialog-box';
import { NameModel } from '../../../../../model/requestModel/NameModel';
import { CommonFun } from '../../../../../utils/helper/CommonFun';

@Component({
  selector: 'app-find-friend',
  imports: [DialogBox],
  templateUrl: './find-friend.html',
  styleUrl: './find-friend.scss',
})
export class FindFriend implements OnInit {

  private platformId = inject(PLATFORM_ID);
  userList = signal<any[]>([]);
  showModal = false;
  selectedUserName: string | null = null;
  modalTitle = '';
  modalMessage = '';
  status = '';

  temp: any;

  constructor(private userService: UserService, private commonFunctionObject: CommonFun) {


  }


  ngOnInit(): void {
    console.log('----find-friend user module running--------ngOnInit------');

    if (isPlatformBrowser(this.platformId)) {

      this.userService.getAllUserList().subscribe({
        next: (res) => {
         this.userList.set( JSON.parse(JSON.stringify(res)).data);
        }
      })
    }
  }

  serachFriend() {
    alert("search button");
  }

  sendRequest(userName: string, status: string) {
    this.selectedUserName = userName;
    this.showModal = true;

    this.status = status;
    if(status == "ACCEPT")
    {
      this.modalTitle = 'Accept Request';
      this.modalMessage = 'Are you sure you want to accept the request?';
    }
    else{
       this.modalTitle = 'Send Request';
      this.modalMessage = 'Are you sure you want to send the request?';
    }
  }

  closeModal() {
    this.showModal = false;
    this.selectedUserName = null;
  }

  onConfirm(result: boolean) {
    this.showModal = false;
    if(result && this.status == "ACCEPT" && this.selectedUserName !== null)
    {
      this.acceptFriendRequest(this.selectedUserName);
    }
    else if (result && this.selectedUserName !== null) {
      this.sendFriendRequest(this.selectedUserName);
    }
    this.selectedUserName = null;
  }

  private acceptFriendRequest(userName: string ){
    const requestData: NameModel = new NameModel(userName);

     if (isPlatformBrowser(this.platformId)) {
      this.userService.acceptFriendRequest(requestData).subscribe({
        next: (res) => {
            this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
        },
        error: (e) => {
          //bad credentials
          if (e.status == 404 || e.status == 409) {
            this.commonFunctionObject.openSnackBar(e.error.message, 'danger');
          }
          // else if (e.status == 409) {
          //  this.commonFunctionObject.openSnackBar(e.error.message, 'danger');
          // }
        }
      })
    }
  }

  private sendFriendRequest(userName: string) {

    const requestData: NameModel = new NameModel(userName);

    if (isPlatformBrowser(this.platformId)) {
      this.userService.sendFriendRequest(requestData).subscribe({
        next: (res) => {
            this.commonFunctionObject.openSnackBar(JSON.parse(JSON.stringify(res)).message, 'success');
        },
        error: (e) => {
          //bad credentials
          if (e.status == 404 || e.status == 409) {
            this.commonFunctionObject.openSnackBar(e.error.message, 'danger');
          }
          // else if (e.status == 409) {
          //  this.commonFunctionObject.openSnackBar(e.error.message, 'danger');
          // }
        }
      })
    }
  }

}
