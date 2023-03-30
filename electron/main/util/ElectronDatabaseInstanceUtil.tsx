import Storage from "electron-store";
import path from "path";
import { TypedJSON } from "typedjson";
import { StorageSpaceModel, DatabaseModel } from "@/model";
import { getBaseFolderPath } from "@/util/StorageUtil";

export const DatabaseStorageFolderName = "ed069481-6ae2-8360-61e5-66f528c13020";

export async function getDatabase() {
  const databaseStorageFolder = path.join(
    await getBaseFolderPath(),
    DatabaseStorageFolderName
  );
  const databaseStorage = new Storage<{
    StorageSpaceList: StorageSpaceModel[];
  }>({
    cwd: databaseStorageFolder,
    defaults: {
      StorageSpaceList: [],
    },
    deserialize: (data) => {
      return new TypedJSON(DatabaseModel).parse(data) as any;
    },
  });
  return databaseStorage;
}

