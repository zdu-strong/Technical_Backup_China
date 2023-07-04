import { CapacitorConfig } from '@capacitor/cli';
import path from 'path'

const config: CapacitorConfig =
{
  appId: "com.zdu.capacitor",
  appName: "my-app",
  webDir: "build",
  loggingBehavior: "none",
  android: {
    buildOptions: {
      releaseType: "APK",
      keystorePath: path.join(__dirname, "bin/Test.keystore"),
      keystorePassword: "123456",
      keystoreAlias: "android",
      keystoreAliasPassword: "123456",
      signingType: "apksigner",
    }
  },
};

export default config;