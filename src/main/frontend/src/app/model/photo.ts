import {ExifData} from "./exif-data";
import {Tag} from "./tag";

export class Photo {
  id:number;
  createdDate: any;
  modifiedDate:any;
  createdBy:any;
  updatedBy:any;
  version:any;

  path:string;
  name:string;
  extension:string;
  description:string
  size:number;
  exifData:ExifData;
  tags:Tag;
}
