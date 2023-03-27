import { makeAutoObservable } from 'mobx-react-use-autorun';
import { jsonMember, jsonObject } from 'typedjson'

@jsonObject
export class UserEmailModel {

  @jsonMember(String)
  id?: string;

  @jsonMember(String)
  email!: string;

  @jsonMember(String)
  verificationCode!: string;

  constructor() {
    makeAutoObservable(this);
  }
}