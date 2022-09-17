import { jsonMember, jsonObject } from 'typedjson'
import { UserModel } from './UserModel';

@jsonObject
export class FriendshipModel {

  @jsonMember(String)
  id?: string = null as any;

  @jsonMember(Boolean)
  isFriend?: boolean | undefined = null as any;

  @jsonMember(Boolean)
  isBlacklist?: boolean | undefined = null as any;

  @jsonMember(Boolean)
  isFriendOfFriend?: boolean | undefined = null as any;

  @jsonMember(Boolean)
  isBlacklistOfFriend?: boolean | undefined = null as any;

  @jsonMember(Date)
  createDate?: Date | undefined = null as any;

  @jsonMember(Date)
  updateDate?: Date | undefined = null as any;

  @jsonMember(UserModel)
  user?: UserModel | undefined = null as any;

  @jsonMember(UserModel)
  friend?: UserModel | undefined = null as any;

  @jsonMember(String)
  aesOfUser?: string | undefined = null as any;

  @jsonMember(String)
  aesOfFriend?: string | undefined = null as any;

}