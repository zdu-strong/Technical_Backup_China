import { StatusBar } from '@capacitor/status-bar'
import { ScreenOrientation } from '@awesome-cordova-plugins/screen-orientation'
import { Capacitor } from '@capacitor/core'
import { AndroidFullScreen } from '@awesome-cordova-plugins/android-full-screen'

export async function LANDSCAPE() {
  if (Capacitor.getPlatform() === "web") {
    return;
  }

  ScreenOrientation.lock(ScreenOrientation.ORIENTATIONS.LANDSCAPE);
  await AndroidFullScreen.immersiveMode()
  await StatusBar.hide();
  await StatusBar.setOverlaysWebView({ overlay: true });
}

export async function PORTRAIT_PRIMARY() {
  if (Capacitor.getPlatform() === "web") {
    return;
  }

  ScreenOrientation.lock(ScreenOrientation.ORIENTATIONS.PORTRAIT_PRIMARY);
  await StatusBar.setOverlaysWebView({ overlay: false });
  await AndroidFullScreen.showSystemUI()
  await StatusBar.show();
}

async function initScreenOrientation() {
  await PORTRAIT_PRIMARY()
}

export const initValue = initScreenOrientation()