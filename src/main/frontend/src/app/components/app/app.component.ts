import {Component, OnInit} from '@angular/core';
import {FolderService} from "../../services/folder.service";
import {Folder} from "../../model/folder";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent  implements OnInit {
  directory:string;
  rootFolder:Folder;
  selectedFolder:Folder;

  constructor(private folderService: FolderService) { }

  ngOnInit() {
    this.directory = btoa("2017.06.26 Lublin");
    this.getFolder();
  }

  getFolder(): void {
    this.folderService.getRootFolder()
      .subscribe(folders => this.rootFolder = folders);
  }
}
