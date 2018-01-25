import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Rx';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import {HttpClient, HttpHeaders, HttpRequest} from "@angular/common/http";

@Injectable()
export class AuthenticationService {
  private authUrl = 'api/auth';
  private headers = new HttpHeaders({'Content-Type': 'application/json'});

  constructor(private http: HttpClient) {
  }

  login(username: string, password: string): Observable<boolean> {
    return this.http.post(this.authUrl, JSON.stringify({username: username, password: password}), {headers: this.headers})
      .map((response: Response) => {
        // login successful if there's a jwt token in the response
        if (response) {
          let token:string = (<any>response).token;
          // store username and jwt token in local storage to keep user logged in between page refreshes
          localStorage.setItem('currentUser', JSON.stringify({ username: username, token: token }));

          // return true to indicate successful login
          return true;
        } else {
          // return false to indicate failed login
          return false;
        }
      }).catch(
        (error:any) =>
          Observable.throw(error.error.message || 'Server error')
      );
  }

  getToken(): String {
    var currentUser = JSON.parse(localStorage.getItem('currentUser'));
    var token = currentUser && currentUser.token;
    return token ? token : "";
  }

  logout(): void {
    // clear token remove user from local storage to log user out
    localStorage.removeItem('currentUser');
  }

  isLoggedIn(): boolean {
    var token: String = this.getToken();
    return token && token.length > 0;
  }
}
