import { jsonMember, jsonObject } from "typedjson";

@jsonObject
export class GitPropertiesModel {

  @jsonMember(String)
  commitId: string = null as any;

  @jsonMember(Date)
  commitDate: Date = null as any;

}

