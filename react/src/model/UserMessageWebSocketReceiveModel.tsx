import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { UserMessageModel } from './UserMessageModel';

@jsonObject
export class UserMessageWebSocketReceiveModel {

  @jsonMember(Number)
  totalPage!: number;

  @jsonArrayMember(UserMessageModel)
  list!: UserMessageModel[];
}