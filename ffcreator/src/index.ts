import path from 'path';
import { FFCreator, FFImage, FFScene } from 'ffcreator';
import { timer } from 'rxjs';
import ImageSize from 'image-size';
import fs from 'fs';

async function generateVideo(config: {
  cacheDir: string;
  outputVideoFilePath: string;
}) {
  // 预先定义的变量
  const audioOneFilePathOfAreYouOk = path.join(
    __dirname,
    'audio',
    '雷军 - Are You OK.mp3',
  );
  const paddyOne = path.join(__dirname, 'image', 'paddyOne.png');
  const paddyTwo = path.join(__dirname, 'image', 'paddyTwo.png');
  const paddyThree = path.join(__dirname, 'image', 'paddyThree.png');
  const paddyFour = path.join(__dirname, 'image', 'paddyFour.png');
  const width = 800;
  const height = 550;
  // 视频的基础配置
  const ffcreator = new FFCreator({
    cacheDir: config.cacheDir,
    output: config.outputVideoFilePath,
    width: width,
    height: height,
    audioLoop: false,
    fps: 100,
    debug: false,
    defaultOutputOptions: {
      options: ['-c:v libvpx-vp9', '-c:a libopus', '-pix_fmt yuv420p'],
    },
    log: true,
  });
  {
    // 背景音乐
    ffcreator.addAudio({
      path: audioOneFilePathOfAreYouOk,
      start: 0,
      volume: 1,
      bg: true,
      loop: true,
    });
  }
  {
    // 场景1, 图片白背景
    const ffscene = new FFScene();
    ffscene.setBgColor('#FFFFFF');
    ffscene.setDuration(3);
    ffscene.setTransition('Colorful', 3);
    ffcreator.addChild(ffscene);
  }
  {
    // 场景2, 图片第一张
    const ffscene = new FFScene();
    ffscene.setBgColor('#30336b');
    ffscene.setDuration(5);
    ffscene.setTransition('MoveLeft', 1);
    ffscene.addChild(
      await imageOnCenter({
        width,
        height,
        imagePath: paddyOne,
      }),
    );
    ffcreator.addChild(ffscene);
  }
  {
    // 场景3, 图片第二张
    const ffscene = new FFScene();

    ffscene.setBgColor('#FFFFFF');
    ffscene.setDuration(5);
    ffscene.setTransition('InvertedPageCurl', 1);
    const ffimage = await imageOnCenter({
      width,
      height,
      imagePath: paddyTwo,
    });

    ffimage.addEffect('fadeIn', 0, 0);
    ffimage.addAnimate({
      from: { scale: 1, alpha: 1 },
      to: {
        x: Math.floor(width / 2),
        y: Math.floor(height / 2),
        scale: 0.1,
        alpha: 0,
      },
      time: 1.5,
      delay: 1,
      ease: 'Quadratic.In',
    });
    ffimage.addAnimate({
      from: {
        x: Math.floor(width / 2),
        y: Math.floor(height / 2),
        scale: 0.1,
        alpha: 0,
      },
      to: { scale: 1, alpha: 1 },
      time: 1.5,
      delay: 2.5,
      ease: 'Quadratic.In',
    });
    ffscene.addChild(ffimage);
    ffcreator.addChild(ffscene);
  }
  {
    // 场景4, 图片第三张
    const ffscene = new FFScene();

    ffscene.setBgColor('#30336b');
    ffscene.setDuration(5);
    ffscene.setTransition('Radial', 1);
    ffscene.addChild(
      await imageOnCenter({
        width,
        height,
        imagePath: paddyThree,
      }),
    );
    ffcreator.addChild(ffscene);
  }
  {
    // 场景5, 图片第四张
    const ffscene = new FFScene();
    ffscene.setBgColor('#30336b');
    ffscene.setDuration(2);
    ffscene.addChild(
      await imageOnCenter({
        width,
        height,
        imagePath: paddyFour,
      }),
    );
    ffcreator.addChild(ffscene);
  }
  return ffcreator;
}

// 图片放在场景正中间, 上下居中, 左右居中
async function imageOnCenter(config: {
  width: number;
  height: number;
  imagePath: string;
}) {
  const imageInfo = ImageSize(config.imagePath);
  const imageWidth =
    config.width / config.height > imageInfo.width / imageInfo.height
      ? Math.floor(config.height * (imageInfo.width / imageInfo.height))
      : config.width;
  const imageHeight =
    config.width / config.height > imageInfo.width / imageInfo.height
      ? config.height
      : Math.floor(config.width * (imageInfo.height / imageInfo.width));
  const ffimage = new FFImage({
    path: config.imagePath,
    resetXY: true,
    x: Math.floor((config.width - imageWidth) / 2),
    y: Math.floor((config.height - imageHeight) / 2),
    width: imageWidth,
    height: imageHeight,
  });
  return ffimage;
}

async function main() {
  const cacheDir = process.env.FFCREATOR_CACHE_DIRECTORY;
  const outputVideoFilePath = process.env.FFCREATOR_OUTPUT_VIDEO_FILE_PATH;
  if (!cacheDir || !cacheDir.trim()) {
    throw new Error('CacheDir(FFCREATOR_CACHE_DIRECTORY) can not be empty');
  }
  if (!outputVideoFilePath || !outputVideoFilePath.trim()) {
    throw new Error(
      'OutputVideoFilePath(FFCREATOR_OUTPUT_VIDEO_FILE_PATH) can not be empty',
    );
  }
  const creator = await generateVideo({
    cacheDir,
    outputVideoFilePath,
  });
  const resultPromise = new Promise((resolve, reject) => {
    creator.on('error', (e) => {
      reject(new Error(e.error || `FFCreator has error`));
    });
    creator.on('complete', () => {
      resolve(`FFCreator completed `);
    });
  });
  await creator.start();
  await resultPromise;
  await fs.promises.rm(cacheDir, { recursive: true, force: true });

  console.log('\n');
  for (let i = 10; i > 0; i--) {
    await timer(1).toPromise();
  }
  console.log('\n');
  process.exit();
}

export default main();
