import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";

@jsonObject
export class UserModel {
  @jsonMember(String)
  id!: string;

  @jsonMember(String)
  username!: string;

  constructor() {
    makeAutoObservable(this);
  }
}
