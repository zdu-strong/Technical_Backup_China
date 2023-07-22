import path from 'path'
import os from 'os'
import inquirer from "inquirer"
import linq from 'linq'
import execa from "execa"
import treeKill from 'tree-kill'
import util from 'util'
import fs from 'fs'
import CapacitorConfig from '../capacitor.config'

async function main() {
  const isRunAndroid = await getIsRunAndroid();
  const androidSdkRootPath = await getAndroidSdkRootPath();
  await addPlatformSupport(isRunAndroid);
  const deviceList = await getDeviceList(isRunAndroid);
  await buildReact();
  await runAndroidOrIOS(isRunAndroid, androidSdkRootPath, deviceList);
  await copySignedApk(isRunAndroid);
  process.exit();
}

async function runAndroidOrIOS(isRunAndroid: boolean, androidSdkRootPath: string, deviceList: string[]) {
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
        "ANDROID_HOME": `${androidSdkRootPath}`
      } : {
      }) as any,
    }
  );
  await updateDownloadAddressOfGradleZipFile(isRunAndroid);
  await updateDownloadAddressOfGrableDependencies(isRunAndroid);
  await addAndroidPermissions(isRunAndroid);
  await execa.command(
    [
      `cap run ${isRunAndroid ? "android" : "ios"}`,
      "--no-sync",
      `${deviceList.length === 1 ? `--target=${linq.from(deviceList).single()}` : ''}`,
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
      `cap build`,
      ...(isRunAndroid ? [`--keystorepath ${path.relative(path.join(__dirname, "..", "android"), CapacitorConfig.android!.buildOptions!.keystorePath!)}`] : []),
      ...(isRunAndroid ? [`--keystorepass ${CapacitorConfig.android?.buildOptions?.keystorePassword}`] : []),
      ...(isRunAndroid ? [`--keystorealias ${CapacitorConfig.android?.buildOptions?.keystoreAlias}`] : []),
      ...(isRunAndroid ? [`--keystorealiaspass ${CapacitorConfig.android?.buildOptions?.keystoreAliasPassword}`] : []),
      ...(isRunAndroid ? [`--signing-type ${CapacitorConfig.android?.buildOptions?.signingType}`] : []),
      ...(isRunAndroid ? [`--androidreleasetype ${CapacitorConfig.android?.buildOptions?.releaseType}`] : []),
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
  await childProcess;
  await util.promisify(treeKill)(childProcess.pid!).catch(async () => null);
  await Promise.resolve(null);
  await signSithApksigner(isRunAndroid, androidSdkRootPath);
}

async function signSithApksigner(isRunAndroid: boolean, androidSdkRootPath: string) {
  if (!isRunAndroid) {
    return;
  }
  const apkPath = path.join(__dirname, "..", "android/app/build/outputs/apk/release", "app-release-signed.apk");
  await fs.promises.rm(apkPath, { force: true, recursive: true });
  const filePathOfUnsignedApk = path.join(apkPath, "..", "app-release-unsigned.apk");
  await fs.promises.copyFile(filePathOfUnsignedApk, apkPath);
  const apkSignerParentFolderPath = await getApkSignerParentFolderPath();
  await execa.command(
    [
      "apksigner",
      "sign",
      `--ks ${path.relative(path.join(__dirname, "..", "android/app/build/outputs/apk/release"), CapacitorConfig.android!.buildOptions!.keystorePath!)}`,
      `--ks-pass pass:${CapacitorConfig.android?.buildOptions?.keystorePassword}`,
      `--ks-key-alias ${CapacitorConfig.android?.buildOptions?.keystoreAlias}`,
      `--pass-encoding utf-8`,
      `app-release-signed.apk`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, "..", "android/app/build/outputs/apk/release"),
      extendEnv: true,
      env: {
        "ANDROID_HOME": `${androidSdkRootPath}`,
        ...(os.platform() === "win32" ? {
          Path: `${process.env.Path};${path.normalize(apkSignerParentFolderPath)}`
        } : {}),
        ...(os.platform() !== "win32" ? {
          PATH: `${process.env.PATH}:${path.normalize(apkSignerParentFolderPath)}`
        } : {}),
      } as any,
    },
  )
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

async function getAndroidSdkRootPath() {
  let androidSdkRootPath = path.join(os.homedir(), "AppData/Local/Android/sdk").replace(new RegExp("\\\\", "g"), "/");
  if (os.platform() === "darwin") {
    androidSdkRootPath = path.join(os.homedir(), "Android/Sdk").replace(new RegExp("\\\\", "g"), "/");
  }
  return androidSdkRootPath;
}

async function getApkSignerParentFolderPath() {
  const androidSdkRootPath = await getAndroidSdkRootPath();
  const buildToolsPath = path.join(androidSdkRootPath, "build-tools");
  const fileNameList = await fs.promises.readdir(buildToolsPath);
  const fileName = linq.from(fileNameList).select(s => s.split(".")).select(s => linq.from(s).first()).select(s => Number(s)).orderByDescending(s => s).first();
  const apkSignerParentFolderPath = path.join(buildToolsPath, linq.from(fileNameList).where(s => s.startsWith(String(fileName) + ".")).first());
  return apkSignerParentFolderPath;
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
    console.log("\n");
    console.log("Copied apk to new location.");
    console.log(filePathOfNewApk);
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
}

async function getDeviceList(isRunAndroid: boolean) {
  let deviceList = [] as string[];
  if (isRunAndroid) {
    await execa.command(
      `cap run ${isRunAndroid ? 'android' : 'ios'} --list`,
      {
        stdio: "inherit",
        cwd: path.join(__dirname, ".."),
      }
    );
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
    deviceList = linq.from(androidDeviceOutputList)
      .skip(startIndex + 1)
      .select(item => linq.from(item.split(new RegExp("\\s\\s+")))
        .select(item => item.trim()).toArray()
      )
      .where(s => s.some(m => m.trim() === "API 33"))
      .groupBy(() => "")
      .selectMany(s => {
        if (s.count() > 1) {
          return s.where(m => m.some(n => n.includes("Pixel 6"))).toArray();
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

export default main()