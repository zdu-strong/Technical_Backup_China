import { jsonMember, jsonObject } from 'typedjson'

@jsonObject
export class UserSignInVerificationCodeModel {

  @jsonMember(String)
  id: string = null as any;

  @jsonMember(String)
  verificationCode?: string = null as any;

}