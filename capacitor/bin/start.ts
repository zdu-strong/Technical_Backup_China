import execa from 'execa'
import getPort from 'get-port'
import treeKill from 'tree-kill'
import util from 'util'
import path from 'path'
import os from 'os'
import inquirer from "inquirer"
import linq from 'linq'
import waitOn from 'wait-on'
import fs from 'fs'
import axios from 'axios'

async function main() {
  if (isTestEnvironment()) {
    await runCapacitorForCypress();
    process.exit();
  }
  const avaliablePort = await getPort();
  const isRunAndroid = await getIsRunAndroid();
  const androidSdkRootPath = getAndroidSdkRootPath();
  await addPlatformSupport(isRunAndroid);
  const deviceList = await getDeviceList(isRunAndroid);
  await buildReact();
  const { childProcessOfReact } = await startReact(avaliablePort);
  const [childProcessOfCapacitor] = await createChildProcessOfCapacitor(isRunAndroid, avaliablePort, androidSdkRootPath, deviceList);
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

async function startReact(avaliablePort: number) {
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

  await Promise.race([childProcessOfReact, waitOn({ resources: [`http://127.0.0.1:${avaliablePort}`] })]);
  for (let i = 2000; i > 0; i--) {
    await axios.get(`http://127.0.0.1:${avaliablePort}`);
  }
  return { childProcessOfReact };
}

function isTestEnvironment() {
  return process.env.CAPACITOR_CYPRESS_IS_TEST === "true";
}

async function runCapacitorForCypress() {
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
  if (os.platform() !== "win32") {
    androidSdkRootPath = path.join(os.homedir(), "Android/Sdk").replace(new RegExp("\\\\", "g"), "/");
  }
  return androidSdkRootPath;
}

async function createChildProcessOfCapacitor(isRunAndroid: boolean, avaliablePort: number, androidSdkRootPath: string, deviceList: string[]) {
  await execa.command(
    [
      `cap sync`,
      "--deployment",
      `${isRunAndroid ? "android" : "ios"}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_HOME": `${androidSdkRootPath}`
      } : {
      }) as any,
    }
  );
  await updateDownloadAddressOfGradleZipFile(isRunAndroid);
  await updateDownloadAddressOfGrableDependencies(isRunAndroid);
  await addAndroidPermissions(isRunAndroid);
  await usesCleartextTraffic(isRunAndroid);
  await execa.command(
    [
      `cap run`,
      "--no-sync",
      `${deviceList.length === 1 ? `--target=${linq.from(deviceList).single()}` : ''}`,
      `${isRunAndroid ? "android" : "ios"}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_HOME": `${androidSdkRootPath}`,
      } : {
      }) as any,
    }
  );
  const childProcess = execa.command(
    [
      `cap run`,
      `--no-sync`,
      `--live-reload`,
      `--port ${avaliablePort}`,
      `${deviceList.length === 1 ? `--target ${linq.from(deviceList).single()}` : ''}`,
      `${isRunAndroid ? 'android' : "ios"}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_HOME": `${androidSdkRootPath}`,
      } : {
      }) as any,
    }
  );
  return [childProcess];
}

async function addPlatformSupport(isRunAndroid: boolean) {
  await execa.command(
    [
      `cap add`,
      `${isRunAndroid ? 'android' : 'ios'}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
}

async function getDeviceList(isRunAndroid: boolean) {
  let deviceList = [] as string[];
  if (isRunAndroid) {
    await execa.command(
      [
        `cap run`,
        `--list`,
        `${isRunAndroid ? 'android' : 'ios'}`
      ].join(" "),
      {
        stdio: "inherit",
        cwd: path.join(__dirname, ".."),
      }
    );
    const { stdout: androidDeviceOutput } = await execa.command(
      [
        `cap run`,
        `--list`,
        `${isRunAndroid ? 'android' : 'ios'}`,
      ].join(" "),
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
    deviceList = linq.from(androidDeviceOutputList)
      .skip(startIndex + 1)
      .select(item => linq.from(item.split(new RegExp("\\s\\s+")))
        .select(item => item.trim()).toArray()
      )
      .where(s => s.some(m => m.trim() === "API 33"))
      .groupBy(() => "")
      .selectMany(s => {
        if (s.count() > 1) {
          return s.where(m => m.some(n => n.includes("Pixel 7"))).toArray();
        }
        return s.toArray();
      })
      .select(s => linq.from(s)
        .last()
      )
      .toArray();
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

async function updateDownloadAddressOfGradleZipFile(isRunAndroid: boolean) {
  if (!isRunAndroid) {
    return;
  }
  const filePathOfGradlePropertiesFile = path.join(__dirname, "..", "android", "gradle", "wrapper", "gradle-wrapper.properties");
  const text = await fs.promises.readFile(filePathOfGradlePropertiesFile, "utf8");
  const replaceText = text.replace("https\\://services.gradle.org/distributions/", "https\\://mirrors.cloud.tencent.com/gradle/");
  await fs.promises.writeFile(filePathOfGradlePropertiesFile, replaceText);
}

async function updateDownloadAddressOfGrableDependencies(isRunAndroid: boolean) {
  if (!isRunAndroid) {
    return;
  }
  {
    const filePathOfGradlePropertiesFile = path.join(__dirname, "..", "android", "build.gradle");
    const text = await fs.promises.readFile(filePathOfGradlePropertiesFile, "utf8");
    const replaceText = text.replace(new RegExp("google\\(\\)\\s+mavenCentral\\(\\)", "ig"), `maven{ url 'https://maven.aliyun.com/repository/google' }\n        maven{ url 'https://maven.aliyun.com/repository/central' }`);
    await fs.promises.writeFile(filePathOfGradlePropertiesFile, replaceText);
  }
  {
    const filePathOfGradlePropertiesFile = path.join(__dirname, "..", "android", "capacitor-cordova-android-plugins", "build.gradle");
    const text = await fs.promises.readFile(filePathOfGradlePropertiesFile, "utf8");
    let replaceText = text.replace(new RegExp("google\\(\\)\\n        mavenCentral\\(\\)", "ig"), `maven{ url 'https://maven.aliyun.com/repository/google' }\n        maven{ url 'https://maven.aliyun.com/repository/central' }`);
    replaceText = replaceText.replace(new RegExp("google\\(\\)\\n    mavenCentral\\(\\)", "ig"), `maven{ url 'https://maven.aliyun.com/repository/google' }\n    maven{ url 'https://maven.aliyun.com/repository/central' }`);
    await fs.promises.writeFile(filePathOfGradlePropertiesFile, replaceText);
  }
}

async function addAndroidPermissions(isRunAndroid: boolean) {
  if (!isRunAndroid) {
    return;
  }
  const androidManifestFilePath = path.join(__dirname, "..", "android/app/src/main", "AndroidManifest.xml");
  const content = await fs.promises.readFile(androidManifestFilePath, { encoding: "utf-8" });
  const textList = linq.from(content.split("\r\n")).selectMany(s => s.split("\n")).toArray();
  const permissionList = [
    `    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />`,
    `    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`,
  ];
  const index = textList.findIndex(s => s.includes("</manifest>"));
  if (index < 0) {
    throw new Error("no manifest tag found")
  }
  textList.splice(index, 0, ...permissionList);
  await fs.promises.writeFile(androidManifestFilePath, textList.join("\n"), "utf8");
}

async function usesCleartextTraffic(isRunAndroid: boolean) {
  if (!isRunAndroid) {
    return;
  }
  const androidManifestFilePath = path.join(__dirname, "..", "android/app/src/main", "AndroidManifest.xml");
  const content = await fs.promises.readFile(androidManifestFilePath, { encoding: "utf-8" });
  const textList = linq.from(content.split("\r\n")).selectMany(s => s.split("\n")).toArray();
  const applicationAttributeList = [
    `        android:usesCleartextTraffic="true"`,
  ];
  const index = textList.findIndex(s => s.includes("    <application"));
  if (index < 0) {
    throw new Error("no application tag found")
  }
  textList.splice(index + 1, 0, ...applicationAttributeList);
  await fs.promises.writeFile(androidManifestFilePath, textList.join("\n"), "utf-8");
}

export default main()