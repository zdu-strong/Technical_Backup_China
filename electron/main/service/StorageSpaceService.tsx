import { PaginationModel } from "@/model";
import { ElectronStorage, ElectronDatabase } from "@/util";
import linq from "linq";
import { v1 } from "uuid";
import { subMilliseconds } from "date-fns";

export async function getStorageSpaceListByPagination(pageNum: number, pageSize: number) {
  const database = await ElectronDatabase.getDatabase();

  /* Pagination */
  const stream = linq
    .from(database.get("StorageSpaceList"))
    .orderBy((s) => s.createDate)
    .thenBy(s => s.id);
  return PaginationModel(pageNum, pageSize, stream);
}

export async function isUsed(folderName: string) {
  const database = await ElectronDatabase.getDatabase();

  /* Get folder name from file path */
  const folderNameOfRelative = await ElectronStorage.getFolderNameBaseOnBaseFolderPath(folderName);

  /* Has been used, The file path to store the json file of the database */
  if (folderNameOfRelative === ElectronDatabase.DatabaseStorageFolderName) {
    while (true) {
      const StorageSpaceList = database.get("StorageSpaceList");
      const index = StorageSpaceList.findIndex(s => s.folderName === folderNameOfRelative);
      if (index >= 0) {
        StorageSpaceList.splice(index, 1);
        database.set("StorageSpaceList", StorageSpaceList);
        continue;
      }
      break;
    }

    return true;
  }

  if (await isUsedByProgramData(folderNameOfRelative)) {
    while (true) {
      const StorageSpaceList = database.get("StorageSpaceList");
      const index = StorageSpaceList.findIndex(s => s.folderName === folderNameOfRelative);
      if (index >= 0) {
        StorageSpaceList.splice(index, 1);
        database.set("StorageSpaceList", StorageSpaceList);
        continue;
      }
      break;
    }
    return true;
  }

  /* Save data to database */
  await createStorageSpaceEntityIfNotExist(folderNameOfRelative);

  /* Check if it is used */
  const tempFileValidTime = 24 * 60 * 60 * 1000;
  const expiredDate = subMilliseconds(new Date(), 0 - tempFileValidTime);

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
  const database = await ElectronDatabase.getDatabase();

  /* Get folder name from file path */
  const folderNameOfRelative = await ElectronStorage.getFolderNameBaseOnBaseFolderPath(
    folderName
  );

  /* Do not delete when in use */
  if (await isUsed(folderNameOfRelative)) {
    return;
  }

  /* Delete from disk */
  await ElectronStorage.deleteFolderOrFile(folderNameOfRelative);

  /* Delete from database */
  const StorageSpaceList = database.get("StorageSpaceList");
  const storageSpaceEntityList = linq
    .from(StorageSpaceList)
    .where((s) => s.folderName === folderNameOfRelative)
    .toArray();
  for (const storageSpaceEntity of storageSpaceEntityList) {
    StorageSpaceList.splice(StorageSpaceList.indexOf(storageSpaceEntity), 1);
  }
  database.set("StorageSpaceList", StorageSpaceList);
}

async function isUsedByProgramData(folderName: string) {
  return false;
}

async function createStorageSpaceEntityIfNotExist(folderName: string) {
  const database = await ElectronDatabase.getDatabase();

  /* Do not add if it already exists */
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
