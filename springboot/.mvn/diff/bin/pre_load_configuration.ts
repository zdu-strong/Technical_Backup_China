import { execSync } from 'child_process'
import fs from 'fs'
import path from 'path'

async function main() {
  await deletePackageLockFile();
  await deleteBuildFolder();
  await deleteDiffFiles();
  await deleteDistFolder();
  await installDependencies();
  process.exit();
};

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
        "npm_config_canvas_binary_host_mirror": "https://npmmirror.com/mirrors/canvas",
        "npm_config_package_lock": "false",
      } as any,
    }
  );
}

async function deletePackageLockFile() {
  const filePathOfPackageLockFile = path.join(__dirname, "..", "package-lock.json");
  await fs.promises.rm(filePathOfPackageLockFile, { recursive: true, force: true });
}

async function deleteBuildFolder() {
  const folderPath = path.join(__dirname, "..", "build");
  await fs.promises.rm(folderPath, { recursive: true, force: true });
}

async function deleteDiffFiles() {
  await fs.promises.rm(path.join(__dirname, "..", "index.exe"), { recursive: true, force: true });
  await fs.promises.rm(path.join(__dirname, "..", "index-linux"), { recursive: true, force: true });
  await fs.promises.rm(path.join(__dirname, "..", "index-macos"), { recursive: true, force: true });
  await fs.promises.rm(path.join(__dirname, "..", "index-win.exe"), { recursive: true, force: true });
  await fs.promises.rm(path.join(__dirname, "..", "diff.exe"), { recursive: true, force: true });
  await fs.promises.rm(path.join(__dirname, "..", "diff.dmg"), { recursive: true, force: true });
  await fs.promises.rm(path.join(__dirname, "..", "diff"), { recursive: true, force: true });
  await fs.promises.rm(path.join(__dirname, "..", "diff-linux"), { recursive: true, force: true });
  await fs.promises.rm(path.join(__dirname, "..", "diff-macos"), { recursive: true, force: true });
}

async function deleteDistFolder() {
  await fs.promises.rm(path.join(__dirname, "..", "dist"), { recursive: true, force: true });
}

export default main()