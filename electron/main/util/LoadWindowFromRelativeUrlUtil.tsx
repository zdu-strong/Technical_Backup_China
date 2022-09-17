import { BrowserWindow } from "electron";
import path from "path";
import { getBuildFoldePath } from "./GetBuildFolderPathUtil";
import { isPackaged } from "./IsPackagedUtil";

export async function loadWindowFromRelativeUrl(
  browserWindow: BrowserWindow,
  url: string
) {
  if (!url.startsWith("/")) {
    throw new Error("Unsupport url");
  }
  if (isPackaged) {
    await browserWindow.loadFile(path.join(getBuildFoldePath, "index.html"), {
      hash: new URL("http://127.0.0.1" + url).pathname,
      search: new URL("http://127.0.0.1" + url).search,
    });
  } else {
    await browserWindow.loadURL(
      new URL(url, `http://127.0.0.1:${process.env.ELECTRON_PORT}`).toString()
    );
  }
}
