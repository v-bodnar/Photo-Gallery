import {Observable} from 'rxjs/Observable';
import {Injectable} from '@angular/core';
import {
  HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest,
  HttpResponse
} from '@angular/common/http';
import {MessageService} from "primeng/components/common/messageservice";

@Injectable()
export class AuthenticationInterceptor implements HttpInterceptor {


  constructor(public messageService: MessageService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.getToken()) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${this.getToken()}`
        }
      });
      return next.handle(request).do(
        (event: HttpEvent<any>) => {
          if (event instanceof HttpResponse) {
            // do stuff with response if you want
          }
        }, (err: any) => {
          if (err instanceof HttpErrorResponse) {
            if (err.status === 403) {
              this.messageService.add({severity: 'error', summary: 'Access Denied', detail: 'Current user does not have access to some functions'})
            }
          }
        }
      );
    }else {
      return next.handle(request).do(
        (event: HttpEvent<any>) => {
          if (event instanceof HttpResponse) {
            // do stuff with response if you want
          }
        }, (err: any) => {
          if (err instanceof HttpErrorResponse) {
            if (err.status === 403) {
              this.messageService.add({severity: 'error', summary: 'Access Denied', detail: 'Current user does not have access to some functions'})
            }
          }
        }
      );
    }
  }


  getToken(): String {
    var currentUser = JSON.parse(localStorage.getItem('currentUser'));
    var token = currentUser && currentUser.token;
    return token ? token : "";
  }
}
