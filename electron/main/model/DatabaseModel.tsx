import { jsonArrayMember, jsonObject } from "typedjson";
import { StorageSpaceModel } from "@/model/StorageSpaceModel";

@jsonObject
export class DatabaseModel {
  @jsonArrayMember(StorageSpaceModel)
  StorageSpaceList: StorageSpaceModel[] = null as any;
}
