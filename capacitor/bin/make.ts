import path from 'path'
import os from 'os'
import inquirer from "inquirer"
import linq from 'linq'
import execa from "execa"
import fs from 'fs'

async function main() {
  await checkPlatform();
  const isRunAndroid = await getIsRunAndroid();
  const androidSdkRootPath = await getAndroidSdkRootPath();
  await getDeviceList(isRunAndroid);
  await buildReact();
  await runAndroidOrIOS(isRunAndroid, androidSdkRootPath);
  await copySignedApk(isRunAndroid);
  process.exit();
}

async function runAndroidOrIOS(isRunAndroid: boolean, androidSdkRootPath: string) {
  if (isRunAndroid) {
    await execa.command("npx -y -p typescript -p ts-node ts-node --skipProject bin/update_gradle.ts");
    await execa.command(
      [
        "ionic capacitor build android",
        "--no-build",
        "--prod",
        "--no-open",
      ].join(" "),
      {
        stdio: "inherit",
        cwd: path.join(__dirname, ".."),
        extendEnv: true,
        env: {
          "ANDROID_SDK_ROOT": `${androidSdkRootPath}`
        },
      }
    );
    await execa.command(
      [
        "npx cap build android",
      ].join(" "),
      {
        stdio: "inherit",
        cwd: path.join(__dirname, ".."),
        extendEnv: true,
        env: {
          "ANDROID_SDK_ROOT": `${androidSdkRootPath}`
        },
      }
    );
  } else {
    await execa.command(
      [
        "ionic capacitor build ios",
        "--no-build",
        "--prod",
        "--no-open",
      ].join(" "),
      {
        stdio: "inherit",
        cwd: path.join(__dirname, ".."),
        extendEnv: true,
      }
    );
    await execa.command(
      [
        "npx cap build ios",
      ].join(" "),
      {
        stdio: "inherit",
        cwd: path.join(__dirname, ".."),
        extendEnv: true,
      }
    );
  }
}

async function buildReact() {
  await execa.command(
    [
      "react-app-rewired build",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
        "GENERATE_SOURCEMAP": "false",
      },
    }
  );
}

async function checkPlatform() {
  if (os.platform() !== "win32" && os.platform() !== "darwin") {
    throw new Error("The development of linux has not been considered yet");
  }
}

async function getAndroidSdkRootPath() {
  let androidSdkRootPath = path.join(os.homedir(), "AppData/Local/Android/sdk").replace(new RegExp("\\\\", "g"), "/");
  if (os.platform() === "darwin") {
    androidSdkRootPath = path.join(os.homedir(), "Android/Sdk").replace(new RegExp("\\\\", "g"), "/");
  }
  return androidSdkRootPath;
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

async function getDeviceList(isRunAndroid: boolean) {
  let deviceList = [] as string[];
  if (isRunAndroid) {
    const { stdout: androidDeviceOutput } = await execa.command(
      `ionic cap run ${isRunAndroid ? 'android' : 'ios'} --list`,
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
    deviceList = linq.from(androidDeviceOutputList).skip(startIndex + 1).select(item => linq.from(item.split("|")).select(item => item.trim()).toArray()).select(s => linq.from(s).last()).toArray();
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

async function copySignedApk(isRunAndroid: boolean) {
  if (isRunAndroid) {
    const apkPath = path.join(__dirname, "..", "android/app/build/outputs/apk/release", "app-release-signed.apk");
    const filePathOfNewApk = path.join(__dirname, "..", "app-release-signed.apk");
    await fs.promises.copyFile(apkPath, filePathOfNewApk);
  }
}

export default main()