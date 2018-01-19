import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';


import {AppComponent} from './components/app/app.component';
import {Angular2ImageGalleryModule} from "angular2-image-gallery/src/app";
import {FolderService} from "./services/folder.service";
import {HttpClientModule} from "@angular/common/http";
import {
  AutoCompleteModule, CalendarModule, ChipsModule, DialogModule, FileUploadModule, GrowlModule, MenubarModule,
  MessageModule,
  TreeModule
} from "primeng/primeng";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TagService} from "./services/tag.service";
import { UploadDialogComponent } from './components/upload-dialog/upload-dialog.component';
import {FileService} from "./services/file.service";
import {MessageService} from "primeng/components/common/messageservice";

@NgModule({
  declarations: [
    AppComponent,
    UploadDialogComponent
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
    MessageModule
  ],
  providers: [FolderService, TagService, FileService, MessageService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
