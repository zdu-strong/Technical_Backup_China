import execa from 'execa'
import getPort from 'get-port'
import treeKill from 'tree-kill'
import util from 'util'
import path from 'path'

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
            'cypress open'
        ].join(' '),
        {
            stdio: 'inherit',
            cwd: path.join(__dirname, '..'),
            extendEnv: true,
            env: {
                "CYPRESS_BASE_URL": `http://127.0.0.1:${avaliablePort}`,
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

    const childProcessOfWaitOn = execa.command(`wait-on http://127.0.0.1:${avaliablePort}`);
    await Promise.race([childProcessOfReact, childProcessOfWaitOn]);
    return { avaliablePort, childProcessOfReact };
}


export default main()