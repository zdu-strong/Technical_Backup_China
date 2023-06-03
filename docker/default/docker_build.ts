import { execSync } from "child_process"
import path from 'path'

async function main() {
  await buildReact();
  await buildSpringboot();
  await buildFFCreator();
  await runCapacitorTest();
  await runElectronTest();
  await buildMysqlDiff();
  await buildCloud();
}

async function runElectronTest() {
  execSync(
    [
      "docker build",
      "-t electron",
      "-f ./Dockerfile",
      "../../../",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, "./electron"),
    }
  );
}

async function runCapacitorTest() {
  execSync(
    [
      "docker build",
      "-t capacitor",
      "-f ./Dockerfile",
      "../../../",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, "./capacitor"),
    }
  );
}

async function buildReact() {
  execSync(
    [
      "docker build",
      "-t react",
      "-f ./Dockerfile",
      "../../../",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, "./client"),
    }
  );
}

async function buildSpringboot() {
  execSync(
    [
      "docker build",
      "-t springboot",
      "-f ./Dockerfile",
      "../../../",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, "./server"),
    }
  );
}

async function buildFFCreator() {
  execSync(
    [
      "docker build",
      "-t ffcreator",
      "-f ./Dockerfile",
      "../../../",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, "./ffcreator"),
    }
  );
}

async function buildMysqlDiff() {
  execSync(
    [
      "docker build",
      "-t mysqldiff",
      "-f ./Dockerfile",
      "../../../",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, "./MysqlDiff"),
    }
  );
}

async function buildCloud() {
  execSync(
    [
      "docker build",
      "-t cloud",
      "-f ./Dockerfile",
      "../../../",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, "./AliyunCloud"),
    }
  );
}

export default main()