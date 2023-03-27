import { makeAutoObservable } from 'mobx-react-use-autorun';
import { jsonArrayMember, jsonObject } from 'typedjson'
import { FriendshipModel } from './FriendshipModel';
import { PaginationModel } from './PaginationModel';

@jsonObject
export class FriendshipPaginationModel extends PaginationModel {

  @jsonArrayMember(FriendshipModel)
  list!: FriendshipModel[];

  constructor() {
    super();
    makeAutoObservable(this);
  }
}