import { StorageSpaceService } from "@/service";
import { concatMap, lastValueFrom, of, retry, repeat } from "rxjs";
import { from } from "linq";
import { ElectronStorage } from "@/util";

async function runManageStorageSpace() {
  const { totalPage } = await StorageSpaceService.getStorageSpaceListByPagination(1, 1);
  for (let i = totalPage; i > 0; i--) {
    try {
      const result = await StorageSpaceService.getStorageSpaceListByPagination(i, 1);
      for (const storageSpaceModel of result.list) {
        if (!(await StorageSpaceService.isUsed(storageSpaceModel.folderName))) {
          await StorageSpaceService.deleteFolder(storageSpaceModel.folderName);
        }
      }
    } catch (error) {
      // do nothing
    }
  }

  const folderNameListOfRootFolder = await ElectronStorage.listRoots();
  for (const folderName of folderNameListOfRootFolder) {
    try {
      if (!(await StorageSpaceService.isUsed(folderName))) {
        await StorageSpaceService.deleteFolder(folderName);
      }
    } catch (error) {
      // do nothing
    }
  }
}

async function main() {
  await lastValueFrom(
    of(null).pipe(
      concatMap(() => {
        return from(runManageStorageSpace());
      }),
      repeat({ delay: 10 * 60 * 1000 }),
      retry(),
    )
  );
}

export default main
