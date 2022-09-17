import { jsonMember, jsonObject } from 'typedjson'

@jsonObject
export class UserEmailModel {

  @jsonMember(String)
  id?: string;

  @jsonMember(String)
  email!: string;

  @jsonMember(String)
  verificationCode!: string;
}