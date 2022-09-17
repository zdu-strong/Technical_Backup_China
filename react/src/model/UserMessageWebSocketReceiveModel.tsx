import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { UserMessageModel } from './UserMessageModel';

@jsonObject
export class UserMessageWebSocketReceiveModel {

  @jsonMember(Number)
  totalPage: number = null as any;

  @jsonArrayMember(UserMessageModel)
  list: UserMessageModel[] = null as any;
}