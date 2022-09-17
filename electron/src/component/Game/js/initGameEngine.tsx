import { Scene, WebGLEngine } from 'oasis-engine'
import { Camera, MeshRenderer, PrimitiveMesh } from "oasis-engine"
import { OrbitControl } from "@oasis-engine-toolkit/controls";
import { Animator, AssetType, BackgroundMode } from "oasis-engine";
import { Entity, GLTFResource, Layer } from 'oasis-engine'
import { RenderFace, RenderTarget, Script, SkyBoxMaterial } from 'oasis-engine'
import { Texture2D, TextureCube, UnlitMaterial } from 'oasis-engine'
import ImageOne from '../image/image01.gltf'
import ImageTwo from '../image/image02.jpeg'
import ImageThree from '../image/image03.jpeg'
import ImageFour from '../image/image04.jpeg'
import ImageFive from '../image/image05.jpeg'
import ImageSix from '../image/image06.jpeg'
import ImageSeven from '../image/image07.jpeg'

export async function initGameEngine(canvasRef: React.MutableRefObject<any>) {

  const engine = new WebGLEngine(canvasRef.current);
  // Adapter to screen
  engine.canvas.resizeByClientSize();

  // Create scene
  await createScene(engine);

  engine.run();

  return engine;
}

async function createScene(engine: WebGLEngine) {
  const scene = engine.sceneManager.activeScene;
  const rootEntity = scene.createRootEntity();
  const cameraEntity = rootEntity.createChild("camera");
  cameraEntity.addComponent(Camera);
  cameraEntity.transform.setPosition(0, 0, 10);
  const control = cameraEntity.addComponent(OrbitControl);
  control.minDistance = 3;

  scene.ambientLight.diffuseSolidColor.setValue(1, 1, 1, 1);

  // Create planes to mock mirror
  const planeEntity = rootEntity.createChild("mirror");
  const planeRenderer = planeEntity.addComponent(MeshRenderer);
  const mesh = PrimitiveMesh.createPlane(engine, 2, 2);
  const material = new UnlitMaterial(engine);

  planeEntity.transform.setRotation(90, 0, 0);
  material.renderFace = RenderFace.Double;
  planeRenderer.mesh = mesh;
  planeRenderer.setMaterial(material);
  for (let i = 0; i < 8; i++) {
    const clone = planeEntity.clone();
    planeEntity.parent.addChild(clone);

    clone.layer = Layer.Layer1;
    clone.transform.setPosition((i - 4) * 2, 0, i % 2 ? -5 : -8);
  }
  planeEntity.isActive = false;

  // Create sky
  await createSky(engine, scene);
  await loadGLTFResource(engine, rootEntity);

  // Add script to switch `camera.renderTarget`
  cameraEntity.addComponent(await getSwitchRTScript(engine, material));
}

async function getSwitchRTScript(engine: WebGLEngine, material: UnlitMaterial) {
  class SwitchRTScript extends Script {
    renderColorTexture = new Texture2D(engine, 1024, 1024);
    renderTarget = new RenderTarget(engine, 1024, 1024, this.renderColorTexture);

    constructor(entity: Entity) {
      super(entity);
      material.baseTexture = this.renderColorTexture;
    }

    onBeginRender(camera: Camera) {
      camera.renderTarget = this.renderTarget;
      camera.cullingMask = Layer.Layer0;
      camera.render();
      camera.renderTarget = null;
      camera.cullingMask = Layer.Everything;
    }
  }
  return SwitchRTScript;
}

async function loadGLTFResource(engine: WebGLEngine, rootEntity: Entity) {
  // Load glTF
  const gltf = await engine.resourceManager
    .load<GLTFResource>(ImageOne);
  const { animations, defaultSceneRoot } = gltf;

  rootEntity.addChild(defaultSceneRoot);
  const animator = defaultSceneRoot.getComponent(Animator);
  animator.play(animations![0].name);
}

async function createSky(engine: WebGLEngine, scene: Scene) {
  const background = scene.background;
  const sky = background.sky;
  const skyMaterial = new SkyBoxMaterial(engine);
  background.mode = BackgroundMode.Sky;
  sky.material = skyMaterial;
  sky.mesh = PrimitiveMesh.createCuboid(engine, 1, 1, 1);

  {
    const cubeMap = await engine.resourceManager.load<TextureCube>({
      urls: [
        ImageTwo,
        ImageThree,
        ImageFour,
        ImageFive,
        ImageSix,
        ImageSeven,
      ],
      type: AssetType.TextureCube
    })
    scene.ambientLight.specularTexture = cubeMap;
    skyMaterial.textureCubeMap = cubeMap;
  }
}