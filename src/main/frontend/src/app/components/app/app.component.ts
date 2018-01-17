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
  folders: Folder[];
  selectedFolder: Folder;
  galleryName: string;
  menuItems: MenuItem[];
  dateFrom: Date;
  dateTo: Date;
  tags: string[] = [];
  suggestedTags: string[];

  filters:string;

  constructor(private folderService: FolderService) {
  }

  ngOnInit() {
    this.getFolder();
    this.galleryName = 'root';
    this.buildTopMenu();
  }


  getFolder(): void {
    this.folderService.getRootFolder()
      .subscribe((folders: Folder) => {
        this.folders = [new Folder(folders)];
      });
  }

  onGallerySelect() {
    console.log(this.selectedFolder.path);
    if(this.selectedFolder.path == "\\"){
      this.selectedFolder = undefined;
    }
    this.constructFilters();
    this.buildTopMenu();
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
    this.galleryName = ( this.selectedFolder == undefined ? 'root' : this.selectedFolder.name )+ Math.random()

  }

  buildTopMenu(){
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

  constructFilters() {
    this.galleryName = ( this.selectedFolder == undefined ? 'root' : this.selectedFolder.name )+ Math.random()

    if (this.selectedFolder !== undefined && this.tags.length !== 0 && this.dateFrom !== undefined && this.dateTo !== undefined) {
      this.filters = "/findByDirectoryAndTagsLikeAndDateBetween/" + Base64.encode(this.selectedFolder.path) + "/" + this.tags + "/" + this.formatDate(this.dateFrom) + "/" + this.formatDate(this.dateTo);
    } else if (this.selectedFolder !== undefined && this.tags.length !== 0) {
      this.filters = "/findByDirectoryAndTagsLike/" + Base64.encode(this.selectedFolder.path) + "/" + this.tags
    } else if (this.selectedFolder !== undefined && this.dateFrom !== undefined && this.dateTo !== undefined) {
      this.filters = "/findByDirectoryAndDateBetween/" + Base64.encode(this.selectedFolder.path) + "/" + this.formatDate(this.dateFrom) + "/" + this.formatDate(this.dateTo);
    } else if (this.tags.length !== 0 && this.dateFrom !== undefined && this.dateTo !== undefined) {
      this.filters = "/findByTagsLikeAndDateBetween/" + this.tags + "/" + this.formatDate(this.dateFrom) + "/" + this.formatDate(this.dateTo);
    } else if (this.dateFrom !== undefined && this.dateTo !== undefined) {
      this.filters = "/findByDateBetween/" + this.formatDate(this.dateFrom) + "/" + this.formatDate(this.dateTo);
    } else if (this.tags.length !== 0) {
      this.filters = "/findByTagsLike/" + this.tags;
    } else if (this.selectedFolder !== undefined) {
      this.filters = "/findByDirectory/" + Base64.encode(this.selectedFolder.path);
    } else {
      this.filters = undefined;
    }
    console.log(this.galleryName);
    console.log(this.filters);
  }

  formatDate(date:Date):string{
    let month = ["01","02","03","04","05","06","07","08","09","10","11","12"]
    return date.getFullYear() + "-" + month[date.getMonth()] + "-" + date.getDate()
  }

  tagSearch(event){
    this.suggestedTags = ['lublin', 'inner', 'барбекю на озере'];
  }
}
