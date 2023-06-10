import { CapacitorConfig } from '@capacitor/cli';
import path from 'path'

const config: CapacitorConfig =
{
  appId: "io.ionic.starter",
  appName: "my-app",
  webDir: "build",
  loggingBehavior: "none",
  android: {
    buildOptions: {
      releaseType: "APK",
      keystorePath: path.join(__dirname, "bin/test.keystore"),
      keystorePassword: "123456",
      keystoreAlias: "testalias",
      keystoreAliasPassword: "123456",
    }
  },
};

export default config;