export class Tag {
  id:number;
  name: string;
  description : string;
  createdDate: any;
  modifiedDate:any;
  createdBy:any;
  updatedBy:any;
  version:any;


  constructor(name: string) {
    this.name = name;
  }
}
