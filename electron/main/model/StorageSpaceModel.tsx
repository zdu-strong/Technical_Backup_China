import { makeAutoObservable } from "mobx";
import { jsonMember, jsonObject } from "typedjson";

@jsonObject
export class StorageSpaceModel {
  @jsonMember(String)
  id!: string;

  @jsonMember(String)
  folderName!: string;

  @jsonMember(Date)
  createDate!: Date;

  @jsonMember(Date)
  updateDate!: Date;

  constructor() {
    makeAutoObservable(this);
  }
}
