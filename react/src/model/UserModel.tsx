import NodeRSA from 'node-rsa';
import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { UserEmailModel } from './UserEmailModel';
import { UserSignInVerificationCodeModel } from './UserSignInVerificationCodeModel';

@jsonObject
export class UserModel {

  @jsonMember(String)
  id?: string = null as any;

  @jsonMember(String)
  username?: string = null as any;

  @jsonMember(String)
  password?: string = null as any;

  @jsonMember(String)
  email?: string = null as any;

  @jsonMember(String)
  publicKeyOfRSA?: string = null as any;

  @jsonMember(String)
  privateKeyOfRSA?: string = null as any;

  @jsonMember(UserSignInVerificationCodeModel)
  userSignInVerificationCode?: UserSignInVerificationCodeModel = null as any;

  @jsonArrayMember(UserEmailModel)
  userEmailList?: UserEmailModel[] = null as any;

  @jsonMember(NodeRSA)
  rsa?: NodeRSA = null as any;
}