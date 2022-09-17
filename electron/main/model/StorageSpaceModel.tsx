import { jsonMember, jsonObject } from "typedjson";

@jsonObject
export class StorageSpaceModel {
  @jsonMember(String)
  id: string = null as any;

  @jsonMember(String)
  folderName: string = null as any;

  @jsonMember(Date)
  createDate: Date = null as any;

  @jsonMember(Date)
  updateDate: Date = null as any;
}
