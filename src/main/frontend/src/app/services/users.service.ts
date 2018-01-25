import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {User} from "../model/user";
import {Authorities} from "../model/authorities";

@Injectable()
export class UsersService {

  constructor(private http: HttpClient) {}

  private usersUrl = 'api/users/';
  private authoritiesUrl = 'api/authorities/';
  getUsers (): Observable<[User]> {
    return this.http.get<[User]>(this.usersUrl);
  }

  createUser (user:User): Observable<User> {
    return this.http.post<User>(this.usersUrl, user);
  }

  updateUser (user:User): Observable<User> {
    return this.http.put<User>(this.usersUrl, user);
  }

  deleteUser (user:User): Observable<User> {
    return this.http.delete<User>(this.usersUrl + "/" + user.id + "/");
  }

  getAuthorities (): Observable<[Authorities]> {
    return this.http.get<[Authorities]>(this.authoritiesUrl);
  }
}
