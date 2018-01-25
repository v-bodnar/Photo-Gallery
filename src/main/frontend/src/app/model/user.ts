import {Authorities} from "./authorities";

export class User {
  id: number;
  username: string;
  password: string;
  description: string;
  createdDate: any;
  modifiedDate: any;
  createdBy: any;
  updatedBy: any;
  version: any;
  authorities: Authorities[];

}
