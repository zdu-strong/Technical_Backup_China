import { UserModel } from "@/model/UserModel";
import axios from "axios";
import NodeRSA from "node-rsa";
import CryptoJS from 'crypto-js'
import { v1 } from "uuid";
import { UserEmailModel } from "@/model/UserEmailModel";
import { getAccessToken, getPrivateKeyOfRSA, GlobalUserInfo, removeAccessToken, removePrivateKeyOfRSA, setAccessToken, setGlobalUserInfo, setPrivateKeyOfRSA } from "@/common/axios-config/AxiosConfig";

export async function getNewAccountOfSignUp() {
  return axios.post<UserModel>("/sign_up/get_new_account");
}

export async function signUp(userId: string, password: string, nickname: string, userEmailList: UserEmailModel[], publicKeyOfRSA: string): Promise<void> {
  await signOut();
  const rsa = new NodeRSA(publicKeyOfRSA, "pkcs8-public", { encryptionScheme: "pkcs1" });
  let newRSA = new NodeRSA();
  newRSA.setOptions({ encryptionScheme: "pkcs1" })
  newRSA = newRSA.generateKeyPair(2048);
  const realPassword = `${Buffer.from(v1(), 'utf-8').toString('base64')}${Buffer.from(new Uint8Array(Array.from(CryptoJS.lib.WordArray.random(32).words))).toString('base64')}`;
  await axios.post("/sign_up", {
    id: userId,
    username: nickname,
    password: realPassword,
    userEmailList: userEmailList,
    publicKeyOfRSA: newRSA.exportKey("pkcs8-public-der").toString("base64"),
    privateKeyOfRSA: CryptoJS.AES.encrypt(JSON.stringify({
      privateKeyOfRSA: newRSA.exportKey("pkcs8-private-der").toString("base64"),
      password: realPassword,
    }), Buffer.from(JSON.stringify([userId, password]), 'utf-8').toString('base64')).toString(),
    email: rsa.encrypt(userId, 'base64'),
  });
  await signIn(userId, password);
}

export async function sendVerificationCode(userId: string, email: string, verificationCode: string) {
  return axios.post("/sign_up/send_verification_code", {
    id: userId,
    userEmailList: [{
      email,
      verificationCode,
    }]
  });
}

export async function signIn(userIdOrEmail: string, password: string): Promise<void> {
  await signOut();


  const { data: user } = await axios.post<UserModel>("/sign_in/get_account", null, { params: { userId: userIdOrEmail } });
  let rsa: NodeRSA;
  let newRSA: NodeRSA;
  try {
    var { privateKeyOfRSA, password: passwordString } = JSON.parse(CryptoJS.AES.decrypt(user.privateKeyOfRSA!, Buffer.from(JSON.stringify([user.id, password]), 'utf-8').toString('base64')).toString(CryptoJS.enc.Utf8)) as any as { privateKeyOfRSA: string; password: string };
    rsa = new NodeRSA(privateKeyOfRSA, "pkcs8-private", { encryptionScheme: "pkcs1" });
    newRSA = new NodeRSA();
    newRSA.setOptions({ encryptionScheme: "pkcs1" })
    newRSA = newRSA.generateKeyPair(2048);
  } catch (error) {
    throw new Error('Incorrect password');
  }

  const { data: accessToken } = await axios.post<string>("/sign_in", null, {
    params: {
      userId: user.id,
      password: rsa.encryptPrivate(JSON.stringify({
        id: user.id,
        password: passwordString,
        userSignInVerificationCode: user.userSignInVerificationCode,
        privateKeyOfRSA: newRSA.encrypt(privateKeyOfRSA, 'base64'),
      }), 'base64'),
    }
  });
  setPrivateKeyOfRSA(newRSA.exportKey("pkcs8-private-der").toString("base64"));
  setAccessToken(accessToken);
  await isSignIn();
}

export async function getUserInfo() {
  return axios.get<UserModel>("/get_user_info");
}

export async function signOut() {
  if (getAccessToken()) {
    await axios.post("/sign_out");
  }
  removeAccessToken();
  removePrivateKeyOfRSA();
}

export async function isSignIn() {
  if (!getAccessToken()) {
    return false;
  }
  try {
    if (!GlobalUserInfo.id) {
      const { data: userInfo } = await getUserInfo();
      const rsaOneString = getPrivateKeyOfRSA();
      const rsaOne = new NodeRSA(rsaOneString!, "pkcs8-private", { encryptionScheme: "pkcs1" });
      const rsaTwoString = rsaOne.decrypt(userInfo.privateKeyOfRSA!, "utf8");
      const rsaTwo = new NodeRSA(rsaTwoString!, "pkcs8-private", { encryptionScheme: "pkcs1" });
      userInfo.rsa = rsaTwo;
      setGlobalUserInfo(userInfo);
    }
    return true;
  } catch {
    return false;
  }
}