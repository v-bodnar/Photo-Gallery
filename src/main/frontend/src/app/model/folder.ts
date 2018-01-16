import {TreeNode} from "primeng/primeng";

export class Folder implements TreeNode {
  name: string;
  path: string;
  children: Folder[];

  label?: string;
  data?: any;
  expandedIcon?: any;
  collapsedIcon?: any;
  selectable?: boolean;


  constructor(object: any) {
    this.children = [];
    this.name = object.name;
    this.path = object.path;
    if (object.children !== null && object.children !== undefined) {
      for (let child of object.children) {
        this.children.push(new Folder(child))
      }
    }

    this.label = this.name;
    this.data = this.path;
    this.expandedIcon = "fa-folder-open";
    this.collapsedIcon = "fa-folder";
    this.selectable = true;
  }

  hasChildren(): boolean {
    return this.children !== null && this.children !== undefined && this.children.length > 0;
  }
}
