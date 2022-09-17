import execa from "execa"
import path from 'path'

async function main() {
    await jest();
    process.exit();
}

async function jest() {
    execa.commandSync(
        [
            "jest --config ./test/jest.json",
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
            extendEnv: true,
        }
    );
}

export default main()