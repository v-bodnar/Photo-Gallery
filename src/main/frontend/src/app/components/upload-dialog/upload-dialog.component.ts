import {Component, Host, Input, OnInit} from '@angular/core';
import {FileService} from "../../services/file.service";
import {HttpEventType, HttpResponse} from "@angular/common/http";
import {Tag} from "../../model/tag";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {HomeComponent} from "../home/home.component";

@Component({
  selector: 'upload-form',
  templateUrl: './upload-dialog.component.html',
  styleUrls: ['./upload-dialog.component.css']
})
export class UploadDialogComponent implements OnInit {
  @Input() galleryPath:string;
  tags: string[];
  uploadedFiles: File[] = [];
  uploadForm : FormGroup;
  @Input() galleryNameDisabled : boolean = false;

  constructor(private fileService: FileService,  @Host()public parent: HomeComponent, fb: FormBuilder) {
    this.uploadForm = fb.group({
      'name-input' : [null, Validators.required],
      'tags-input': [[], Validators.required]
    })
  }
  ngOnInit() {
  }

  onUpload(event){
    console.log("onUpload" + event)
  }

  upload(event, form) {
    console.log("upload" + event)
    this.validateAllFormFields(this.uploadForm);
    if (this.uploadForm.valid) {
      let tagList: Tag[] = [];
      for (let tagName of this.tags) {
        tagList.push(new Tag(tagName))
      }
      let path = this.parent.selectedFolder === undefined ? this.galleryPath : this.parent.selectedFolder.path + "\\" + this.galleryPath;
      this.fileService.uploadGallery(event.files, tagList, path).subscribe(event => {
        if (event.type === HttpEventType.UploadProgress) {
          console.log(Math.round(100 * event.loaded / event.total))
          //this.progress.percentage = Math.round(100 * event.loaded / event.total);
        } else if (event instanceof HttpResponse) {
          console.log('File is completely uploaded!');
          if(this.galleryNameDisabled){
            this.parent.displayPhotoDialog = false;
          }else {
            this.parent.displayUploadDialog = false;
          }
          this.parent.getFolder();
          this.parent.getAllTags();
          this.parent.messageService.add({severity: 'success', summary: 'Success', detail: 'Added new Gallery'});
          this.parent.galleryName = ( this.parent.selectedFolder == undefined ? 'root' : this.parent.selectedFolder.name )+ Math.random()
          form.msgs=[];
          form.clear();
          this.uploadForm.reset();
        }
      })
    }
  }

  validateAllFormFields(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach(field => {
      const control = formGroup.get(field);
      if(field == "name-input" && this.galleryNameDisabled){
        control.setValidators(null);
        control.updateValueAndValidity();
      }
      if (control instanceof FormControl) {
        control.markAsTouched({ onlySelf: true });
      } else if (control instanceof FormGroup) {
        this.validateAllFormFields(control);
      }
    });
  }
}
