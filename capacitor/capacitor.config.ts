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
      keystorePath: path.join(__dirname, "bin/MyApk.jks"),
      keystorePassword: "123456",
      keystoreAlias: "MyApk",
      keystoreAliasPassword: "MyApk123456",
    }
  },
};

export default config;