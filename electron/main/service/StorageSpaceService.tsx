import { PaginationModel } from "../model";
import { ElectronStorage, ElectronDatabase } from "../util";
import linq from "linq";
import { v1 } from "uuid";
import { subMilliseconds } from "date-fns";

export async function getStorageSpaceListByPagination(pageNum: number, pageSize: number) {
  const database = await ElectronDatabase.getDatabase();
  const stream = linq
    .from(database.get("StorageSpaceList"))
    .orderBy((s) => s.createDate);
  return PaginationModel(pageNum, pageSize, stream);
}

async function createStorageSpaceEntityIfNotExist(folderName: string) {
  /* Do not add if it already exists */
  const database = await ElectronDatabase.getDatabase();
  const StorageSpaceList = database.get("StorageSpaceList");
  if (
    linq
      .from(StorageSpaceList)
      .where((s) => s.folderName === folderName)
      .any()
  ) {
    return;
  }

  /* Add a row of data */
  StorageSpaceList.push({
    id: v1(),
    folderName: folderName,
    createDate: new Date(),
    updateDate: new Date(),
  });
  database.set("StorageSpaceList", StorageSpaceList);
}

export async function isUsed(folderName: string) {
  /* Save data to database */
  const database = await ElectronDatabase.getDatabase();
  const folderNameOfRelative = await ElectronStorage.getFolderNameBaseOnBaseFolderPath(folderName);
  await createStorageSpaceEntityIfNotExist(folderNameOfRelative);

  /* Has been used */
  if (folderNameOfRelative === ElectronDatabase.DatabaseStorageFolderName) {
    return true;
  }

  /* Check if it is used */
  const tempFileValidTime = 24 * 60 * 60 * 1000;
  const expiredDate = subMilliseconds(new Date(), 0 - tempFileValidTime);
  const list = linq
    .from(database.get("StorageSpaceList"))
    .where((s) => s.folderName === folderNameOfRelative)
    .toArray();
  const isUsed = !linq
    .from(list)
    .where((s) => s.updateDate.getTime() < expiredDate.getTime())
    .where(() =>
      linq
        .from(list)
        .all((m) => m.updateDate.getTime() < expiredDate.getTime())
    )
    .any();
  return isUsed;
}


export async function deleteFolder(folderName: string): Promise<void> {
  /* Do not delete when in use */
  const database = await ElectronDatabase.getDatabase();
  const folderNameOfRelative = await ElectronStorage.getFolderNameBaseOnBaseFolderPath(
    folderName
  );
  if (await isUsed(folderNameOfRelative)) {
    return;
  }

  /* Delete from database */
  const StorageSpaceList = database.get("StorageSpaceList");
  const storageSpaceEntityList = linq
    .from(StorageSpaceList)
    .where((s) => s.folderName === folderName)
    .toArray();
  for (const storageSpaceEntity of storageSpaceEntityList) {
    StorageSpaceList.splice(StorageSpaceList.indexOf(storageSpaceEntity), 1);
  }

  /* Delete from disk */
  database.set("StorageSpaceList", StorageSpaceList);
  await ElectronStorage.deleteFolderOrFile(folderNameOfRelative);
}
