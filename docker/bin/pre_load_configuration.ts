import { execSync } from 'child_process'
import fs from 'fs'
import path from 'path'

async function main() {
  await deletePackageLockFile();
  await installDependencies();
  process.exit();
}

async function installDependencies() {
  execSync(
    [
      "npm install",
      "--no-package-lock",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
}

async function deletePackageLockFile() {
  const filePathOfPackageLockFile = path.join(__dirname, "..", "package-lock.json");
  await fs.promises.rm(filePathOfPackageLockFile, { recursive: true, force: true });
}

export default main()