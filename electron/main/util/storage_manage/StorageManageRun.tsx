import { StorageSpaceService } from "../../service";
import { concat, interval, concatMap, lastValueFrom, of, catchError } from "rxjs";
import { from } from "linq";
import { listRoots } from "../StorageUtil";

async function runManageStorageSpace() {
  const totalPage = (
    await StorageSpaceService.getStorageSpaceListByPagination(1, 1)
  ).totalPage;
  for (let i = totalPage; i > 0; i--) {
    const result = await StorageSpaceService.getStorageSpaceListByPagination(i, 1);
    for (const storageSpaceModel of result.list) {
      if (!(await StorageSpaceService.isUsed(storageSpaceModel.folderName))) {
        await StorageSpaceService.deleteFolder(storageSpaceModel.folderName);
      }
    }
  }
  const folderNameListOfRootFolder = await listRoots();
  for (const folderName of folderNameListOfRootFolder) {
    if (!(await StorageSpaceService.isUsed(folderName))) {
      await StorageSpaceService.deleteFolder(folderName);
    }
  }
}

async function main() {
  await lastValueFrom(
    concat(of(null), interval(60 * 60 * 1000)).pipe(
      concatMap(() => {
        return from(runManageStorageSpace());
      }),
      catchError((error, caught) => caught),
    )
  );
}

export default main
