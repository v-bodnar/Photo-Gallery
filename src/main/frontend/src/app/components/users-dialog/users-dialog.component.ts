import {Component, Host, OnInit} from '@angular/core';
import {UsersService} from "../../services/users.service";
import {User} from "../../model/user";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {HomeComponent} from "../home/home.component";
import {Authorities} from "../../model/authorities";

@Component({
  selector: 'users-dialog',
  templateUrl: './users-dialog.component.html',
  styleUrls: ['./users-dialog.component.css']
})
export class UsersDialogComponent implements OnInit {

  users: User[] = [];
  selectedUser: User;
  userForm: FormGroup;
  roles:Authorities[];

  constructor(private userService: UsersService, fb: FormBuilder, @Host() public parent: HomeComponent) {
    this.userForm = fb.group({
      'username': [null, Validators.required],
      'password': [null, Validators.required],
      'roles': [null, Validators.required]
    })
  }

  ngOnInit() {
    this.getUsers();
    this.getAuthorities();
    this.userForm.reset();
  }

  onRowSelect(event) {
    this.userForm.reset();
    this.selectedUser = event.data;
    this.userForm.get("username").setValue(this.selectedUser.username);
    this.userForm.get("roles").setValue(this.selectedUser.authorities[0]);
    console.log("Selected user: " + this.selectedUser.username)
  }

  onRowUnselect() {
    this.userForm.reset();
    this.selectedUser = null;
  }

  private getUsers() {
    this.userService.getUsers().subscribe(users => {
        this.users = users;
      }
    )
  }

  private getAuthorities() {
    this.userService.getAuthorities().subscribe(authorities => {
        this.roles = authorities;
      }
    )
  }
  private delete(){
    console.log("delete user")
    this.userService.deleteUser(this.selectedUser).subscribe(
      success => {
        this.parent.messageService.add({severity: 'success', summary: 'Success', detail: 'User deleted'})
        this.userForm.reset();
        this.getUsers();
        this.selectedUser = null;
        this.getUsers();
      },
      error => {
        this.parent.messageService.add({severity: 'error', summary: 'Error', detail: error.message})
      }
    );
  }

  public submit() {
    this.validateAllFormFields(this.userForm);
    if (this.userForm.valid) {
      console.log("submit user")
      if (this.selectedUser) {
        this.selectedUser.username = this.userForm.get("username").value;
        this.selectedUser.password = this.userForm.get("password").value;
        this.userService.updateUser(this.selectedUser).subscribe(
          success => {
            this.parent.messageService.add({severity: 'success', summary: 'Success', detail: 'User updated'})
            this.userForm.reset();
            this.selectedUser = null;
            this.getUsers();
          },
          error => {
            this.parent.messageService.add({severity: 'error', summary: 'Error', detail: error.message})
          }
        );
      } else {
        let user: User = new User();
        user.username = this.userForm.get("username").value;
        user.password = this.userForm.get("password").value;
        user.authorities = [this.userForm.get("roles").value];
        this.userService.createUser(user).subscribe(
          success => {
            this.parent.messageService.add({severity: 'success', summary: 'Success', detail: 'User created'})
            this.userForm.reset();
            this.getUsers();
            this.selectedUser = null;
          },
          error => {
            this.parent.messageService.add({severity: 'error', summary: 'Error', detail: error.message})
          }
        );
      }
    }
  }

  validateAllFormFields(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach(field => {
      const control = formGroup.get(field);
      if (control instanceof FormControl) {
        control.markAsTouched({onlySelf: true});
      } else if (control instanceof FormGroup) {
        this.validateAllFormFields(control);
      }
    });
  }

}
