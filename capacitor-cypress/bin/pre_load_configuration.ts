import { execSync } from 'child_process'
import fs from 'fs'
import path from 'path'

async function main() {
  await deletePackageLockFile();
  await deleteBuildFolder();
  await installDependencies();
  await eslint()
  process.exit()
}

async function installDependencies() {
  execSync(
    [
      "npm install",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      env: {
        ...process.env,
        "npm_config_package_lock": "false",
        "CYPRESS_INSTALL_BINARY": `${getCypressBinaryUrl()}`,
      } as any,
    }
  );
}

function getCypressBinaryUrl() {
  const packageJSON = JSON.parse(fs.readFileSync(path.join(__dirname, "..", "package.json")).toString());
  const cypressVersion = packageJSON.devDependencies["cypress"];
  const cypressMirror = "https://npmmirror.com/mirrors/cypress/" + cypressVersion;
  const cypressInstallUrl = cypressMirror + "/" + process.platform + "-" + process.arch + "/cypress.zip";
  return cypressInstallUrl;
}

async function deletePackageLockFile() {
  const filePathOfPackageLockFile = path.join(__dirname, "..", "package-lock.json");
  await fs.promises.rm(filePathOfPackageLockFile, { recursive: true, force: true });
}

async function deleteBuildFolder() {
  const folderPath = path.join(__dirname, "..", "build");
  await fs.promises.rm(folderPath, { recursive: true, force: true });
}

async function eslint() {
  execSync("eslint cypress", {
    stdio: "inherit",
    cwd: path.join(__dirname, "..")
  })
}

export default main()