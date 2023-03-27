import { StorageSpaceService } from "@/service";
import { concat, interval, concatMap, lastValueFrom, of, retry } from "rxjs";
import { from } from "linq";
import { listRoots } from "@/util/StorageUtil";

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

  const folderNameListOfRootFolder = await listRoots();
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
    concat(of(null), interval(60 * 60 * 1000)).pipe(
      concatMap(() => {
        return from(runManageStorageSpace());
      }),
      retry(),
    )
  );
}

export default main
