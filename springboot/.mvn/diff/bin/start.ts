import execa from "execa"
import path from 'path'
import fs from 'fs'
import { timer } from 'rxjs'

async function main() {
  await eslint();
  await build();
  await pack();
  process.exit();
}

async function pack() {
  await execa.command(
    [
      "pkg dist/index.js",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
    }
  );
  for (let i = 100; i > 0; i--) {
    await timer(1).toPromise();
  }
  await fs.promises.rename(path.join(__dirname, "..", "index-linux"), path.join(__dirname, "..", "diff-linux"));
  await fs.promises.rename(path.join(__dirname, "..", "index-macos"), path.join(__dirname, "..", "diff-macos"));
  await fs.promises.rename(path.join(__dirname, "..", "index-win.exe"), path.join(__dirname, "..", "diff.exe"));
}

async function build() {
  await execa.command(
    [
      "nest build",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
    }
  );
}

async function eslint() {
  await execa.command(
    [
      `eslint {src,apps,libs,test}/**/*.ts`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
    }
  );
}

export default main()