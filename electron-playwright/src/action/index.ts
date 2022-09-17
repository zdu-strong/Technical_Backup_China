import path from 'path';
import { _electron } from 'playwright'
import PageClassFunction from '../page'
import os from 'os'

async function 打开程序() {
    const electron = await _electron.launch({
        args: [path.join(__dirname, "../../..", "electron", "dist/index.js")],
        executablePath: getExecutablePath(),
        cwd: path.join(__dirname, "../../..", "electron"),
        locale: "en-US",
    });
    const window = await electron.firstWindow();
    const page = PageClassFunction(window);
    const CPU使用率文字 = await page.主页.当前CPU使用率()
    expect(await CPU使用率文字.isVisible()).toBeTruthy()
    return { electron, window, page }
}

function getExecutablePath() {
    let executablePath = path.join(__dirname, "../../..", "electron");
    if (os.platform() === "win32") {
        executablePath = path.join(executablePath, "node_modules/.bin/electron.cmd");
    } else {
        executablePath = path.join(executablePath, "node_modules/.bin/electron");
    }
    return executablePath;
}

const PageClassType = PageClassFunction(null as any)
export type PageType = typeof PageClassType;
export default {
    打开程序,
}