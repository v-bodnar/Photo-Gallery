import {Component, Inject, OnInit} from '@angular/core';
import {MenuItem, Message} from "primeng/primeng";
import {Base64} from "../../Base64";
import {FolderService} from "../../services/folder.service";
import {Folder} from "../../model/folder";
import {TagService} from "../../services/tag.service";
import {FileService} from "../../services/file.service";
import {Tag} from "../../model/tag";
import {MessageService} from "primeng/components/common/messageservice";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/authentication.service";
import {DOCUMENT} from "@angular/common";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  folders: Folder[];
  selectedFolder: Folder;
  galleryName: string;
  menuItems: MenuItem[];
  dateFrom: Date;
  dateTo: Date;
  tags: string[] = [];
  suggestedTags: string[];
  allTags: string[] = [];
  msgs: Message[] = [];
  origin:string

  filters:string;
  displayUploadDialog:boolean = false;
  displayPhotoDialog:boolean = false;
  displayUsersDialog:boolean = false;

  constructor(private router: Router,
              private folderService: FolderService,
              private tagService: TagService,
              private fileService: FileService,
              public messageService: MessageService,
              private authenticationService: AuthenticationService,
              @Inject(DOCUMENT) private document) {
    this.origin = document.location.origin
  }

  ngOnInit() {
    this.getFolder();
    this.getAllTags();
    this.galleryName = 'root';
    this.buildTopMenu();
  }

  getAllTags():void{
    this.tagService.getTags()
      .subscribe((tags: [Tag]) => {
        this.allTags = tags.map(value => value.name);
      },error => {
        console.error('An error occurred in heroes component, navigating to login: ', error);
      });
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

  logout(){
    this.authenticationService.logout();
    this.router.navigate(['login']);
  }

  buildTopMenu(){
    this.menuItems = [
      {
        label: 'Upload Gallery',
        icon: 'fa-upload',
        command: (event) => this.showUploadDialog()
      }, {
        label: 'Download Gallery',
        icon: 'fa-download',
        command: (event) => this.downloadGallery(),
        disabled: this.selectedFolder === undefined
      }, {
        label: 'Add Photo',
        icon: 'fa-download',
        disabled: this.selectedFolder === undefined,
        command: (event) => this.showPhotoDialog()
      }, {
        label: 'Users',
        icon: 'fa-users',
        command: (event) => this.showUsersDialog()
      }
    ];
  }

  removeTag(event){
    this.tags.splice(this.tags.indexOf(event),1);
    this.constructFilters()
  }

  constructFilters() {
    this.galleryName = ( this.selectedFolder == undefined ? 'root' : this.selectedFolder.name )+ Math.random()

    if (this.selectedFolder !== undefined && this.tags.length !== 0 && (this.dateFrom !== null && this.dateFrom !== undefined) && (this.dateTo !== null && this.dateTo !== undefined)) {
      this.filters = "/findByDirectoryAndTagsLikeAndDateBetween/" + Base64.encode(this.selectedFolder.path) + "/" + this.tags + "/" + this.formatDate(this.dateFrom) + "/" + this.formatDate(this.dateTo);
    } else if (this.selectedFolder !== undefined && this.tags.length !== 0) {
      this.filters = "/findByDirectoryAndTagsLike/" + Base64.encode(this.selectedFolder.path) + "/" + this.tags
    } else if (this.selectedFolder !== undefined && (this.dateFrom !== null && this.dateFrom !== undefined) && (this.dateTo !== null && this.dateTo !== undefined)) {
      this.filters = "/findByDirectoryAndDateBetween/" + Base64.encode(this.selectedFolder.path) + "/" + this.formatDate(this.dateFrom) + "/" + this.formatDate(this.dateTo);
    } else if (this.tags.length !== 0 && (this.dateFrom !== null && this.dateFrom !== undefined) && (this.dateTo !== null && this.dateTo !== undefined)) {
      this.filters = "/findByTagsLikeAndDateBetween/" + this.tags + "/" + this.formatDate(this.dateFrom) + "/" + this.formatDate(this.dateTo);
    } else if ((this.dateFrom !== null && this.dateFrom !== undefined) && (this.dateTo !== null && this.dateTo !== undefined)) {
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
    this.suggestedTags = [];
    for(let tag of this.allTags){
      if(tag.includes(event.query)){
        this.suggestedTags.push(tag);
      }
    }
  }

  downloadGallery(){
    this.fileService.downloadGallery(Base64.encode(this.selectedFolder.path))
      .subscribe(galleryFile => {
        var downloadUrl= window.URL.createObjectURL(galleryFile);
        var anchor = document.createElement("a");
        anchor.download = this.selectedFolder.name + ".zip";
        anchor.href = downloadUrl;
        anchor.click();
      });
  }

  showUploadDialog() {
    this.displayUploadDialog = true;
  }
  showPhotoDialog() {
    this.displayPhotoDialog = true;
  }
  showUsersDialog() {
    this.displayUsersDialog = true;
  }

}
