import { PaginationModel } from "@/model";
import { ElectronStorage, ElectronDatabase } from "@/util";
import linq from "linq";
import { v1 } from "uuid";
import { subMilliseconds } from "date-fns";

export async function getStorageSpaceListByPagination(pageNum: number, pageSize: number) {
  const database = await ElectronDatabase.getDatabase();
  const stream = linq
    .from(database.get("StorageSpaceList"))
    .orderBy((s) => s.createDate)
    .thenBy(s => s.id);
  return PaginationModel(pageNum, pageSize, stream);
}

async function createStorageSpaceEntityIfNotExist(folderName: string) {
  /* Do not add if it already exists */
  const database = await ElectronDatabase.getDatabase();

  if (
    linq
      .from(database.get("StorageSpaceList"))
      .where((s) => s.folderName === folderName)
      .any()
  ) {
    return;
  }

  /* Add a row of data */
  const StorageSpaceList = database.get("StorageSpaceList");
  StorageSpaceList.push({
    id: v1(),
    folderName: folderName,
    createDate: new Date(),
    updateDate: new Date(),
  });
  database.set("StorageSpaceList", StorageSpaceList);
}

export async function isUsed(folderName: string) {
  const folderNameOfRelative = await ElectronStorage.getFolderNameBaseOnBaseFolderPath(folderName);

  /* Save data to database */
  await createStorageSpaceEntityIfNotExist(folderNameOfRelative);

  /* Has been used, The file path to store the json file of the database */
  if (folderNameOfRelative === ElectronDatabase.DatabaseStorageFolderName) {
    return true;
  }

  /* Check if it is used */
  const tempFileValidTime = 24 * 60 * 60 * 1000;
  const expiredDate = subMilliseconds(new Date(), 0 - tempFileValidTime);
  const database = await ElectronDatabase.getDatabase();
  const isUsed = linq
    .from(database.get("StorageSpaceList"))
    .where((s) => s.folderName === folderNameOfRelative)
    .groupBy(s => s.folderName)
    .where(s =>
      s.where(m => m.updateDate.getTime() < expiredDate.getTime())
        .where(() => s.all(m => m.updateDate.getTime() < expiredDate.getTime()))
        .any()
    )
    .any();
  return isUsed;
}


export async function deleteFolder(folderName: string): Promise<void> {
  /* Do not delete when in use */
  const folderNameOfRelative = await ElectronStorage.getFolderNameBaseOnBaseFolderPath(
    folderName
  );
  if (await isUsed(folderNameOfRelative)) {
    return;
  }


  /* Delete from disk */
  await ElectronStorage.deleteFolderOrFile(folderNameOfRelative);

  /* Delete from database */
  const database = await ElectronDatabase.getDatabase();
  const StorageSpaceList = database.get("StorageSpaceList");
  const storageSpaceEntityList = linq
    .from(StorageSpaceList)
    .where((s) => s.folderName === folderName)
    .toArray();
  for (const storageSpaceEntity of storageSpaceEntityList) {
    StorageSpaceList.splice(StorageSpaceList.indexOf(storageSpaceEntity), 1);
  }
  database.set("StorageSpaceList", StorageSpaceList);
}
