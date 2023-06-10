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
  await addPlatformSupport(isRunAndroid);
  await buildReact();
  await runAndroidOrIOS(isRunAndroid, androidSdkRootPath);
  await copySignedApk(isRunAndroid);
  process.exit();
}

async function runAndroidOrIOS(isRunAndroid: boolean, androidSdkRootPath: string) {
  await execa.command(
    [
      `cap sync ${isRunAndroid ? "android" : "ios"}`,
      "--deployment",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_SDK_ROOT": `${androidSdkRootPath}`
      } : {
      }) as any,
    }
  );
  await execa.command(
    [
      `cap build ${isRunAndroid ? "android" : "ios"}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_SDK_ROOT": `${androidSdkRootPath}`
      } : {
      }) as any,
    }
  );
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
      } as any,
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

async function copySignedApk(isRunAndroid: boolean) {
  if (isRunAndroid) {
    const apkPath = path.join(__dirname, "..", "android/app/build/outputs/apk/release", "app-release-signed.apk");
    const filePathOfNewApk = path.join(__dirname, "..", "app-release-signed.apk");
    await fs.promises.copyFile(apkPath, filePathOfNewApk);
  }
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