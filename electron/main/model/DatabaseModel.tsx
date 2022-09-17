import { jsonArrayMember, jsonObject } from "typedjson";
import { StorageSpaceModel } from "./StorageSpaceModel";

@jsonObject
export class DatabaseModel {
  @jsonArrayMember(StorageSpaceModel)
  StorageSpaceList: StorageSpaceModel[] = null as any;
}
