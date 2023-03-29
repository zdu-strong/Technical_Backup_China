import execa from 'execa';
import path from 'path';

async function main() {
  const childProgressFilePath = path.join(__dirname, "StorageManageRun");
  await execa.node(childProgressFilePath, {
    stdio: "inherit",
    cwd: path.join(__dirname, "../../..")
  });
}

export default main