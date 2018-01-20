import {Injectable} from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {Tag} from "../model/tag";
import {ResponseContentType} from "@angular/http";

@Injectable()
export class FileService {
  private uploadUrl = 'api/uploadPhotos/';
  private downloadUrl = 'api/downloadGallery/';

  constructor(private http: HttpClient) {
  }

  uploadGallery(files: File[], tags: Tag[], path: string): Observable<HttpEvent<{}>> {
    let formData: FormData = new FormData();

    for (let file of files) {
      formData.append('files', file, file.name);
    }
    formData.append('tags',JSON.stringify(tags));
    formData.append('path', path);

    const req = new HttpRequest('POST', this.uploadUrl, formData, {
      reportProgress: true,
      responseType: 'text'
    });

    return this.http.request(req);
  }

  downloadGallery (path:string): Observable<Blob> {
    console.log(this.downloadUrl + path)
    return this.http.get(this.downloadUrl + path, {
      responseType: "blob"
    });
  }

}
