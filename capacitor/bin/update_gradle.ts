import path from 'path'
import fs from 'fs'

async function main() {
  await updateDownloadAddressOfGradleZipFile();
  await updateDownloadAddressOfGrableDependencies();
  process.exit();
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