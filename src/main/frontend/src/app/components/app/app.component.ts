import {Component, OnInit} from '@angular/core';
import {FolderService} from "../../services/folder.service";
import {Folder} from "../../model/folder";
import {MenuItem} from 'primeng/primeng';
import {Base64} from "../../Base64";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  directory: string;
  folders: Folder[];
  selectedFolder: Folder;
  galleryName: string;
  menuItems: MenuItem[];
  dateFrom: Date;
  dateTo: Date;
  tags: string[];
  suggestedTags: string[];

  constructor(private folderService: FolderService) {
  }

  ngOnInit() {
    this.getFolder();
    this.galleryName = this.selectedFolder === undefined ? 'root' : this.selectedFolder.name;
    this.buildMenu();
  }


  getFolder(): void {
    this.folderService.getRootFolder()
      .subscribe((folders: Folder) => {
        this.folders = [new Folder(folders)];
      });
  }

  onGallerySelect() {
    console.log(this.selectedFolder.path);
    this.directory = Base64.encode(this.selectedFolder.path);
    this.galleryName = this.selectedFolder == undefined ? 'root' : this.selectedFolder.name;
    this.buildMenu();
  }

  changeView() {
    if (document.getElementById('sidebar').classList.contains("menu-hidden")) {
      document.getElementById('sidebar').classList.remove("menu-hidden");
      document.getElementById('expand-menu-button-icon').classList.add('fa-chevron-left');
      document.getElementById('expand-menu-button-icon').classList.remove('fa-chevron-right');
    } else {
      document.getElementById('sidebar').classList.add("menu-hidden");
      document.getElementById('expand-menu-button-icon').classList.add('fa-chevron-right');
      document.getElementById('expand-menu-button-icon').classList.remove('fa-chevron-left');
    }
    this.galleryName = this.selectedFolder == undefined ? 'root' : this.selectedFolder.name + Math.random();
  }

  buildMenu(){
    this.menuItems = [
      {
        label: 'Upload Gallery',
        icon: 'fa-upload',
      }, {
        label: 'Download Gallery',
        icon: 'fa-download',
        disabled: this.selectedFolder === undefined
      }, {
        label: 'Add Photo',
        icon: 'fa-download',
        disabled: this.selectedFolder === undefined
      }
    ];
  }

  tagSearch(event){
    this.suggestedTags = ['asd','test'];
  }
}
