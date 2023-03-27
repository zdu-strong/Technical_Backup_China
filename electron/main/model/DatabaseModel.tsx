import { jsonArrayMember, jsonObject } from "typedjson";
import { StorageSpaceModel } from "@/model/StorageSpaceModel";
import { makeAutoObservable } from 'mobx'

@jsonObject
export class DatabaseModel {

  @jsonArrayMember(StorageSpaceModel)
  StorageSpaceList!: StorageSpaceModel[];

  constructor() {
    makeAutoObservable(this);
  }

}
