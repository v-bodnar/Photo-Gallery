import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'folders',
  templateUrl: './folder.component.html',
  styleUrls: ['./folder.component.css']
})
export class FolderComponent implements OnInit {
  @Input() folder;

  ngOnInit() {
  }


}
