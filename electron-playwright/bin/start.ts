import execa from 'execa'
import getPort from 'get-port'
import treeKill from 'tree-kill'
import util from 'util'
import path from 'path'
import { timer } from 'rxjs'
import waitOn from 'wait-on'

async function main() {
  const { avaliablePort, childProcessOfElectron } = await startElectron();
  const { childProcessOfPlaywright } = await startPlaywright(avaliablePort);

  await Promise.race([childProcessOfElectron, childProcessOfPlaywright]);
  await util.promisify(treeKill)(childProcessOfElectron.pid!).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfPlaywright.pid!).catch(async () => null);

  process.exit();
}

async function startPlaywright(avaliablePort: number) {
  const childProcessOfPlaywright = execa.command(
    [
      "jest --runInBand --watch --config ./test/jest.json",
    ].join(' '),
    {
      stdio: 'inherit',
      cwd: path.join(__dirname, '..'),
      extendEnv: true,
      env: {
        "ELECTRON_DISABLE_SECURITY_WARNINGS": "true",
        "ELECTRON_PORT": String(avaliablePort),
      }
    }
  );
  return { childProcessOfPlaywright };
}

async function startElectron() {
  const avaliablePort = await getPort();
  const childProcessOfElectron = execa.command(
    [
      'npm start',
    ].join(' '),
    {
      stdio: 'inherit',
      cwd: path.join(__dirname, '../..', "electron"),
      extendEnv: true,
      env: {
        "ELECTRON_PORT": String(avaliablePort),
        "ELECTRON_IS_TEST": "true",
      }
    }
  );
  await Promise.race([childProcessOfElectron, waitOn({ resources: [`http://127.0.0.1:${avaliablePort}`] })]);
  for (let i = 1000; i > 0; i--) {
    await timer(1).toPromise();
  }
  return { avaliablePort, childProcessOfElectron };
}

export default main()