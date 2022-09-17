import execa from 'execa'
import getPort from 'get-port'
import treeKill from 'tree-kill'
import util from 'util'
import path from 'path'
import waitOn from 'wait-on'
import { timer } from 'rxjs'

async function main() {
  const { avaliableClientPort, childProcessOfReact } = await startReact();

  if (process.env.ELECTRON_IS_TEST === "true") {
    await childProcessOfReact;
    await util.promisify(treeKill)(childProcessOfReact.pid!).catch(async () => null);
    process.exit();
  }

  const { childProcessOfElectron } = await startElectron(avaliableClientPort);

  await Promise.race([childProcessOfReact, childProcessOfElectron]);
  await util.promisify(treeKill)(childProcessOfReact.pid!).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfElectron.pid!).catch(async () => null);

  process.exit();
}

async function startElectron(avaliableClientPort: number) {
  const childProcessOfElectron = execa.command(
    [
      "electron .",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
        "ELECTRON_DISABLE_SECURITY_WARNINGS": "true",
        "ELECTRON_PORT": String(avaliableClientPort),
      }
    }
  );
  return { childProcessOfElectron };
}

async function startReact() {
  const avaliableClientPort = Number(process.env.ELECTRON_PORT || await getPort());
  const childProcessOfReact = execa.command(
    [
      "react-app-rewired start",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
        "BROWSER": "NONE",
        "PORT": String(avaliableClientPort),
      }
    }
  );

  await Promise.race([childProcessOfReact, waitOn({ resources: [`http://127.0.0.1:${avaliableClientPort}`] })]);
  for (let i = 1000; i > 0; i--) {
    await timer(1).toPromise();
  }
  return { avaliableClientPort, childProcessOfReact };
}

export default main()