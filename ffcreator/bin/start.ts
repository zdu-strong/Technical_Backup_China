import execa from "execa"
import path from 'path'
import { v1 } from 'uuid'

async function main() {
  const { cacheDir, outputFile } = await getCacheFolderAndOutputFilePath();
  await startFFcreator(cacheDir, outputFile);
  process.exit();
}

async function startFFcreator(cacheDir: string, outputFile: string) {
  await execa.command([
    "npm run production:run",
  ].join(" "), {
    stdio: "inherit",
    cwd: path.join(__dirname, ".."),
    extendEnv: true,
    env: {
      "FFCREATOR_CACHE_DIRECTORY": cacheDir,
      "FFCREATOR_OUTPUT_VIDEO_FILE_PATH": outputFile,
    }
  })
  console.log(`The video path is ${outputFile}`)
  console.log("\n")
}

async function getCacheFolderAndOutputFilePath() {
  const cacheDir = path.join(__dirname, "..", "output", v1());
  const outputFile = path.join(__dirname, "..", "output", v1(), "video.mp4");
  return { cacheDir, outputFile };
}

export default main()