import { jsonArrayMember, jsonObject } from "typedjson";
import { StorageSpaceModel } from "@/model/StorageSpaceModel";
import { makeAutoObservable } from 'mobx-react-use-autorun'

@jsonObject
export class DatabaseModel {

  @jsonArrayMember(StorageSpaceModel)
  StorageSpaceList!: StorageSpaceModel[];

  constructor() {
    makeAutoObservable(this);
  }

}
