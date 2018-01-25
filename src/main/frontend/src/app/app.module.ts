import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';


import {AppComponent} from './components/app/app.component';
import {Angular2ImageGalleryModule} from "angular2-image-gallery/src/app";
import {FolderService} from "./services/folder.service";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {
  AutoCompleteModule, CalendarModule, ChipsModule, DataTableModule, DialogModule, DropdownModule, FileUploadModule,
  GrowlModule,
  MenubarModule,
  MessageModule, TreeModule
} from "primeng/primeng";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TagService} from "./services/tag.service";
import {UploadDialogComponent} from './components/upload-dialog/upload-dialog.component';
import {FileService} from "./services/file.service";
import {MessageService} from "primeng/components/common/messageservice";
import {AppRoutingModule} from './/app-routing.module';
import {LoginComponent} from './components/login/login.component';
import {AuthenticationService} from "./services/authentication.service";
import {CanActivateAuthGuard} from "./services/can-activate-auth-guard";
import {HomeComponent} from './components/home/home.component';
import {AuthenticationInterceptor} from "./services/authentication.interceptor";
import { UsersDialogComponent } from './components/users-dialog/users-dialog.component';
import {UsersService} from "./services/users.service";

@NgModule({
  declarations: [
    AppComponent,
    UploadDialogComponent,
    LoginComponent,
    HomeComponent,
    UsersDialogComponent
  ],
  imports: [
    BrowserModule,
    Angular2ImageGalleryModule,
    HttpClientModule,
    TreeModule,
    MenubarModule,
    CalendarModule,
    FormsModule,
    AutoCompleteModule,
    DialogModule,
    ChipsModule,
    FileUploadModule,
    GrowlModule,
    ReactiveFormsModule,
    MessageModule,
    AppRoutingModule,
    DataTableModule,
    DropdownModule
  ],
  providers: [
    FolderService,
    TagService,
    FileService,
    MessageService,
    AuthenticationService,
    CanActivateAuthGuard,
    UsersService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthenticationInterceptor,
      multi: true,
    }],
  bootstrap: [AppComponent]
})
export class AppModule {
}
