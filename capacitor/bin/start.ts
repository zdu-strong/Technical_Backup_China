import execa from 'execa'
import getPort from 'get-port'
import treeKill from 'tree-kill'
import util from 'util'
import path from 'path'
import os from 'os'
import inquirer from "inquirer"
import linq from 'linq'
import waitOn from 'wait-on'
import { timer } from 'rxjs'
import fs from 'fs'

async function main() {
  await runCapacitorForCypress();
  await checkPlatform();
  const avaliablePort = await getPort();
  const ReactServerAddress = getNetworkAddress(avaliablePort);
  const isRunAndroid = await getIsRunAndroid();
  const androidSdkRootPath = getAndroidSdkRootPath();
  await addPlatformSupport(isRunAndroid);
  const deviceList = await getDeviceList(isRunAndroid);
  await buildReact();
  const { childProcessOfReact } = await startReact(avaliablePort, ReactServerAddress);
  const childProcessOfCapacitor = createChildProcessOfCapacitor(isRunAndroid, ReactServerAddress, androidSdkRootPath, deviceList);
  await Promise.race([childProcessOfReact, childProcessOfCapacitor]);
  await util.promisify(treeKill)(childProcessOfReact.pid!).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfCapacitor.pid!).catch(async () => null);
  process.exit();
}

async function buildReact() {
  const folderPathOfBuild = path.join(__dirname, "..", "build");
  const folderPathOfPublic = path.join(__dirname, "..", "public");
  await fs.promises.cp(folderPathOfPublic, folderPathOfBuild, { recursive: true, force: true });
}

async function startReact(avaliablePort: number, ReactServerAddress: string) {
  const childProcessOfReact = execa.command(
    [
      "react-app-rewired start",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
        "BROWSER": "NONE",
        "PORT": String(avaliablePort),
      } as any,
    }
  );

  await Promise.race([childProcessOfReact, waitOn({ resources: [`${ReactServerAddress}`] })]);
  for (let i = 1000; i > 0; i--) {
    await timer(1).toPromise();
  }
  return { childProcessOfReact };
}

async function checkPlatform() {
  if (os.platform() !== "win32" && os.platform() !== "darwin") {
    throw new Error("The development of linux has not been considered yet");
  }
}

async function runCapacitorForCypress() {
  if (process.env.CAPACITOR_CYPRESS_IS_TEST === "true") {
    await execa.command(
      [
        "react-app-rewired start",
      ].join(" "),
      {
        stdio: "inherit",
        cwd: path.join(__dirname, ".."),
        extendEnv: true,
        env: {
          "BROWSER": "NONE",
        } as any,
      }
    );
    process.exit();
  }
}

async function getIsRunAndroid() {
  let isRunAndroid = true;
  if (os.platform() === "darwin") {
    const MOBILE_PHONE_ENUM = {
      iOS: "iOS",
      Android: "Android"
    };
    const answers = await inquirer.prompt([{
      type: "list",
      name: "mobile phone",
      message: "Do you wish to develop for android or ios?",
      default: MOBILE_PHONE_ENUM.iOS,
      choices: [
        {
          name: MOBILE_PHONE_ENUM.iOS,
          value: MOBILE_PHONE_ENUM.iOS,
        },
        {
          name: MOBILE_PHONE_ENUM.Android,
          value: MOBILE_PHONE_ENUM.Android,
        },
      ],
    }]);
    const chooseAnswer = linq.from(Object.values(answers)).single();
    if (chooseAnswer === MOBILE_PHONE_ENUM.iOS) {
      isRunAndroid = false;
    } else if (chooseAnswer === MOBILE_PHONE_ENUM.Android) {
      isRunAndroid = true;
    } else {
      throw new Error("Please select the type of mobile phone system to be developed!");
    }
  }
  return isRunAndroid;
}

function getAndroidSdkRootPath() {
  let androidSdkRootPath = path.join(os.homedir(), "AppData/Local/Android/sdk").replace(new RegExp("\\\\", "g"), "/");
  if (os.platform() === "darwin") {
    androidSdkRootPath = path.join(os.homedir(), "Android/Sdk").replace(new RegExp("\\\\", "g"), "/");
  }
  return androidSdkRootPath;
}

function createChildProcessOfCapacitor(isRunAndroid: boolean, ReactServerAddress: string, androidSdkRootPath: string, deviceList: string[]) {
  return execa.command(
    [
      `ionic capacitor run ${isRunAndroid ? 'android' : "ios"}`,
      `--livereload-url=${ReactServerAddress}`,
      `${deviceList.length === 1 ? `--target=${linq.from(deviceList).single()}` : ''}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_SDK_ROOT": `${androidSdkRootPath}`,
      } : {
      }) as any,
    }
  );
}

function getNetworkAddress(avaliablePort: number) {
  const stream = linq.from(Object.keys(os.networkInterfaces())).where(name => !(name.includes("(") && name.includes(")"))).select(name => {
    const networkInfoList = os.networkInterfaces()[name];
    const secondNetworkInfoList = [] as any[];
    for (const networkInfo of networkInfoList!) {
      (networkInfo as any).name = name;
      const regex = new RegExp("[0-9]");
      if (regex.test(name)) {
        continue;
      }
      secondNetworkInfoList.push(networkInfo);
    }
    return secondNetworkInfoList;
  }).selectMany(s => s as os.NetworkInterfaceInfo[]).where(item => !item.internal).where(item => item.family === "IPv4");
  const networkAddress = stream.select(item => item.address).select(item => `http://${item}:${avaliablePort}`).single()
  return networkAddress;
}

async function addPlatformSupport(isRunAndroid: boolean) {
  await execa.command(
    `cap add ${isRunAndroid ? 'android' : 'ios'}`,
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
  if (isRunAndroid) {
    await updateDownloadAddressOfGradleZipFile();
    await updateDownloadAddressOfGrableDependencies();
  }
}

async function getDeviceList(isRunAndroid: boolean) {
  let deviceList = [] as string[];
  if (isRunAndroid) {
    const { stdout: androidDeviceOutput } = await execa.command(
      `cap run ${isRunAndroid ? 'android' : 'ios'} --list`,
      {
        stdio: "pipe",
        cwd: path.join(__dirname, ".."),
      }
    );

    const androidDeviceOutputList = linq.from(androidDeviceOutput.split("\r\n")).selectMany(item => item.split("\n")).toArray();
    const startIndex = androidDeviceOutputList.findIndex((item: string) => item.includes('-----'));
    if (startIndex < 0) {
      throw new Error("No available Device!")
    }
    deviceList = linq.from(androidDeviceOutputList).skip(startIndex + 1).select(item => linq.from(item.split(new RegExp("\\s+"))).select(item => item.trim()).toArray()).select(s => linq.from(s).last()).toArray();
    deviceList = deviceList.filter(s => s.startsWith("emulator-"));
    if (!deviceList.length) {
      throw new Error("No available Device!")
    }
    if (deviceList.length === 1) {
      return deviceList;
    }
    throw new Error("More than one available Device!")
  }
  return deviceList;
}

async function updateDownloadAddressOfGradleZipFile() {
  const filePathOfGradlePropertiesFile = path.join(__dirname, "..", "android", "gradle", "wrapper", "gradle-wrapper.properties");
  const text = await fs.promises.readFile(filePathOfGradlePropertiesFile, "utf8");
  const replaceText = text.replace("https\\://services.gradle.org/distributions/", "http\\://mirrors.cloud.tencent.com/gradle/");
  await fs.promises.writeFile(filePathOfGradlePropertiesFile, replaceText);
}

async function updateDownloadAddressOfGrableDependencies() {
  const filePathOfGradlePropertiesFile = path.join(__dirname, "..", "android", "build.gradle");
  const text = await fs.promises.readFile(filePathOfGradlePropertiesFile, "utf8");
  let replaceText = text.replace(`google()\n        mavenCentral()`, `maven{ url 'https://maven.aliyun.com/repository/google' }\n        maven{ url 'https://maven.aliyun.com/repository/central' }`);
  replaceText = replaceText.replace(`google()\n        mavenCentral()`, `maven{ url 'https://maven.aliyun.com/repository/google' }\n        maven{ url 'https://maven.aliyun.com/repository/central' }`);
  await fs.promises.writeFile(filePathOfGradlePropertiesFile, replaceText);
}

export default main()