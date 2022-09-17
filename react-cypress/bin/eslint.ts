import execa from 'execa'
import path from 'path'
import chokidar from 'chokidar'
import { catchError, concatMap, EMPTY, from, of, ReplaySubject, tap, delay, timer } from 'rxjs'
import { exhaustMapWithTrailing } from 'rxjs-exhaustmap-with-trailing'
import cliColor from 'cli-color'

async function main() {
    const changeSubject = new ReplaySubject(1);

    const chokidarWatcher = chokidar.watch([
        path.join(__dirname, "..", "cypress", "e2e"),
        path.join(__dirname, "..", "cypress", "page"),
        path.join(__dirname, "..", "cypress", "action"),
    ]);

    chokidarWatcher.on('all', () => {
        changeSubject.next(null);
    });

    await timer(1000).toPromise();

    changeSubject.pipe(
        exhaustMapWithTrailing(() =>
            of(null).pipe(
                delay(100),
                tap(() => {
                    console.clear()
                }),
                concatMap(() => from(execa.command("eslint cypress", {
                    stdio: "inherit",
                    cwd: path.join(__dirname, ".."),
                    extendEnv: true,
                }))),
                tap(() => {
                    console.log(cliColor.green("\nCompilation complete!"));
                }),
                catchError(() => EMPTY)
            )
        )
    ).subscribe();
}

export default main()