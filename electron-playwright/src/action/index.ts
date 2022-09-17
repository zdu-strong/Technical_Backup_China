import path from 'path';
import { _electron } from 'playwright'
import PageClassFunction, { ProgramType } from '../page'
import os from 'os'

export default {
  async OpenProgram(): Promise<ProgramType> {
    const electron = await _electron.launch({
      args: [path.join(__dirname, "../../..", "electron", "dist/index.js")],
      executablePath: getExecutablePath(),
      cwd: path.join(__dirname, "../../..", "electron"),
      locale: "en-US",
    });
    const window = await electron.firstWindow();
    const page = PageClassFunction(window);
    const CPUUsageText = await page.Home.CurrentCPUUsage()
    expect(await CPUUsageText.isVisible()).toBeTruthy()
    return { electron, window, page }
  }
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