import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './components/app/app.component';
import {FolderComponent} from './components/folder/folder.component';
import {Angular2ImageGalleryModule} from "angular2-image-gallery/src/app";
import {FolderService} from "./services/folder.service";
import {HttpClientModule} from "@angular/common/http";
import { FolderTreeComponent } from './components/folder-tree/folder-tree.component';



@NgModule({
  declarations: [
    AppComponent,
    FolderComponent,
    FolderTreeComponent
  ],
  imports: [
    BrowserModule,
    Angular2ImageGalleryModule,
    HttpClientModule
  ],
  providers: [FolderService],
  bootstrap: [AppComponent]
})
export class AppModule { }
