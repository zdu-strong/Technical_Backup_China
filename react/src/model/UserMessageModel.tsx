import { UserModel } from "@/model/UserModel"
import { jsonMember, jsonObject } from "typedjson";

@jsonObject
export class UserMessageModel {

  @jsonMember(String)
  id: string = null as any;

  @jsonMember(Boolean)
  isDelete: boolean = null as any;

  @jsonMember(Boolean)
  isRecall: boolean = null as any;

  @jsonMember(Date)
  createDate: Date = null as any;

  @jsonMember(Date)
  updateDate: Date = null as any;

  @jsonMember(String)
  content: string = null as any;

  @jsonMember(String)
  url: string = null as any;

  @jsonMember(Number)
  totalPage: number = null as any;

  @jsonMember(Number)
  pageNum: number = null as any;

  @jsonMember(UserModel)
  user: UserModel = null as any;

}

