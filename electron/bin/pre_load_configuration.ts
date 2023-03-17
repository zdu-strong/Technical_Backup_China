import { execSync } from 'child_process'
import fs from 'fs'
import path from 'path'

async function main() {
  await deletePackageLockFile();
  await deleteBuildFolder();
  await deleteDistFolder();
  await deleteOutputFolder();
  await installDependencies();
  await cancelCopyExeForCache();
  await rebuildDependenciesOfElectron();
  await compileCode();
  process.exit();
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
        "npm_config_canvas_binary_host_mirror": "https://npmmirror.com/mirrors/canvas",
        "npm_config_package_lock": "false",
        "ELECTRON_BUILDER_BINARIES_MIRROR": "https://npmmirror.com/mirrors/electron-builder-binaries/",
        "ELECTRON_MIRROR": "https://npmmirror.com/mirrors/electron/",
      } as any,
    }
  );
}

async function deletePackageLockFile() {
  const filePathOfPackageLockFile = path.join(__dirname, "..", "package-lock.json");
  await fs.promises.rm(filePathOfPackageLockFile, { recursive: true, force: true });
}

async function deleteBuildFolder() {
  const folderPathOfBuild = path.join(__dirname, "..", "build");
  await fs.promises.rm(folderPathOfBuild, { recursive: true, force: true });
}

async function deleteOutputFolder() {
  const folderPathOfOutput = path.join(__dirname, "..", "output");
  await fs.promises.rm(folderPathOfOutput, { recursive: true, force: true });
}

async function deleteDistFolder() {
  const folderPathOfDist = path.join(__dirname, "..", "dist");
  await fs.promises.rm(folderPathOfDist, { recursive: true, force: true });
}

async function rebuildDependenciesOfElectron() {
  execSync(
    [
      "electron-builder install-app-deps"
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      env: {
        ...process.env,
        "npm_config_canvas_binary_host_mirror": "https://npmmirror.com/mirrors/canvas",
        "npm_config_package_lock": "false",
        "ELECTRON_BUILDER_BINARIES_MIRROR": "https://npmmirror.com/mirrors/electron-builder-binaries/",
        "ELECTRON_MIRROR": "https://npmmirror.com/mirrors/electron/",
      } as any,
    }
  );
}

async function compileCode() {
  execSync(
    [
      `eslint main/**/*.tsx --config electron.eslintrc.js`
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
  execSync(
    [
      "nest build --path tsconfig.build.json",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
}

async function cancelCopyExeForCache() {
  execSync(
    [
      "npx -y -p typescript -p ts-node ts-node --skipProject bin/cancel_copy_exe_for_cache.ts"
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
}

export default main()