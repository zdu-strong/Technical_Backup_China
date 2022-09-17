import path from 'path'
import fs from 'fs'
import { from, concatMap, toArray, lastValueFrom } from 'rxjs'

async function main() {
  await removeActionOfCopyExeForCache();
  process.exit();
}

async function isExistFile(filePath: string) {
  try {
    const isExist = (await fs.promises.stat(filePath)).isFile();
    return isExist;
  } catch {
    return false;
  }
}

async function removeActionOfCopyExeForCache() {
  const nshOfInstallerJSFilePath = path.join(__dirname, "..", "node_modules/app-builder-lib/templates/nsis/include", "installer.nsh");
  if (await isExistFile(nshOfInstallerJSFilePath)) {
    const textOfNshOfInstallerJSFile = await fs.promises.readFile(nshOfInstallerJSFilePath, { encoding: "utf-8" });
    const textList = await lastValueFrom(from(textOfNshOfInstallerJSFile.split("\r\n")).pipe(
      concatMap((s) => {
        return from(s.split("\n"));
      }),
      toArray(),
    ));
    const textOfNeedRemoveLine = "      !insertmacro copyFile \"$EXEPATH\" \"$LOCALAPPDATA\\${APP_INSTALLER_STORE_FILE}\"";
    const textOfNewContentOfNshOfInstallerJSFile = textList!.filter((s: string) => s !== textOfNeedRemoveLine).join("\n");
    await fs.promises.writeFile(nshOfInstallerJSFilePath, textOfNewContentOfNshOfInstallerJSFile, { encoding: "utf-8" });
  }
}

export default main()