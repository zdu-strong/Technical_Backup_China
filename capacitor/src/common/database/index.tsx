import { StorageSpaceModel, UserModel } from '@/model';
import Dexie, { Table } from 'dexie'

export class Database extends Dexie {
  public UserList!: Table<UserModel, string>; // id is number in this case
  public StorageSpaceList!: Table<StorageSpaceModel, string>;

  public constructor() {
    super("4cb7be90-0909-11ee-a038-d9f47a1d108f");
    this.version(1).stores({
      UserList: "id, username"
    });
    this.version(2).stores({
      StorageSpaceList: "id, folderName, createDate, updateDate"
    });
  }
}