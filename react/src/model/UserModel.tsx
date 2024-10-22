import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { UserEmailModel } from '@/model/UserEmailModel';
import { makeAutoObservable } from 'mobx-react-use-autorun'

@jsonObject
export class UserModel {

  @jsonMember(String)
  id!: string;

  @jsonMember(String)
  username!: string;

  @jsonMember(String)
  email!: string;

  @jsonMember(String)
  publicKeyOfRSA!: string;

  @jsonMember(String)
  privateKeyOfRSA!: string;

  @jsonMember(String)
  password!: string;

  @jsonArrayMember(UserEmailModel)
  userEmailList!: UserEmailModel[];

  @jsonMember(Boolean)
  loading!: boolean;

  encryptByPublicKeyOfRSA!: (data: string) => Promise<string>;

  decryptByPrivateKeyOfRSA!: (data: string) => Promise<string>;

  encryptByPrivateKeyOfRSA!: (data: string) => Promise<string>;

  decryptByPublicKeyOfRSA!: (data: string) => Promise<string>;

  @jsonMember(String)
  accessToken!: string;

  @jsonMember(String)
  secretKeyOfAESOfAccessToken!: string;

  constructor() {
    makeAutoObservable(this);
  }
}