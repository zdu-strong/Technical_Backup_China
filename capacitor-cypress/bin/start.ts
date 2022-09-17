import execa from 'execa'
import getPort from 'get-port'
import treeKill from 'tree-kill'
import util from 'util'
import path from 'path'
import { timer } from 'rxjs'
import waitOn from 'wait-on'

async function main() {
  const { avaliablePort, childProcessOfReact } = await startReact();
  const { childProcessOfCypress } = await startCypress(avaliablePort);

  await Promise.race([childProcessOfReact, childProcessOfCypress]);
  await util.promisify(treeKill)(childProcessOfReact.pid!).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfCypress.pid!).catch(async () => null);

  process.exit();
}

async function startCypress(avaliablePort: number) {
  const childProcessOfCypress = execa.command(
    [
      'cypress open',
      "--e2e"
    ].join(' '),
    {
      stdio: 'inherit',
      cwd: path.join(__dirname, '..'),
      extendEnv: true,
      env: {
        "CYPRESS_BASE_URL": `http://127.0.0.1:${avaliablePort}`,
        "CYPRESS_VERIFY_TIMEOUT": "120000",
      },
    }
  );
  return { childProcessOfCypress };
}

async function startReact() {
  const avaliablePort = await getPort();
  const childProcessOfReact = execa.command(
    [
      'npm start',
    ].join(' '),
    {
      stdio: 'inherit',
      cwd: path.join(__dirname, '../../capacitor'),
      extendEnv: true,
      env: {
        "PORT": `${avaliablePort}`,
        'CAPACITOR_CYPRESS_IS_TEST': "true",
      },
    }
  );

  await Promise.race([childProcessOfReact, waitOn({ resources: [`http://127.0.0.1:${avaliablePort}`] })]);
  for (let i = 1000; i > 0; i--) {
    await timer(1).toPromise();
  }
  return { avaliablePort, childProcessOfReact };
}


export default main()