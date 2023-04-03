import { UserModel } from '@/model';
import { Preferences } from '@capacitor/preferences'
import { TypedJSON } from 'typedjson'

export async function getDatabase() {
  return {
    UserList: await getUserList(),
  };
}

export async function setDatabase(key: "UserList", value: any) {
  await Preferences.set({
    key,
    value: JSON.stringify(value),
  });
}

export async function removeDatabase(key: "UserList") {
  await Preferences.remove({
    key: key
  })
}

async function getUserList() {
  const defaultValue = [] as UserModel[];
  const userList = new TypedJSON(UserModel).parseAsArray((await Preferences.get({ key: "UserList" })).value);
  return userList || defaultValue;
}