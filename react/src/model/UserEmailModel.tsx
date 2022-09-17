import { jsonMember, jsonObject } from 'typedjson'

@jsonObject
export class UserEmailModel {

  @jsonMember(String)
  id?: string = null as any;

  @jsonMember(String)
  email?: string = null as any;

  @jsonMember(String)
  verificationCode?: string | undefined = null as any;
}