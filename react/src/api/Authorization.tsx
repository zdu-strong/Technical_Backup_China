import { UserModel } from "@/model/UserModel";
import axios from "axios";
import { UserEmailModel } from "@/model/UserEmailModel";
import { getAccessToken, getPrivateKeyOfRSA, GlobalUserInfo, removeAccessToken, removePrivateKeyOfRSA, removeUserIdOfGlobalUserInfo, setAccessToken, setGlobalUserInfo, setPrivateKeyOfRSA } from "@/common/axios-config/AxiosConfig";
import { decryptByPrivateKeyOfRSA, encryptByPublicKeyOfRSA, encryptByPrivateKeyOfRSA, decryptByPublicKeyOfRSA, generateKeyPairOfRSA } from "@/common/RSAUtils";
import { decryptByAES, encryptByAES, generateSecretKeyOfAES } from '@/common/AESUtils';

export async function createNewAccountOfSignUp() {
  return axios.post<UserModel>("/sign_up/create_new_account");
}

export async function signUp(userId: string, password: string, nickname: string, userEmailList: UserEmailModel[], publicKeyOfRSA: string): Promise<void> {
  const { privateKey, publicKey } = await generateKeyPairOfRSA();
  await axios.post(`/sign_up`, {
    id: userId,
    username: nickname,
    userEmailList: userEmailList,
    publicKeyOfRSA: publicKey,
    privateKeyOfRSA: await encryptByAES(await generateSecretKeyOfAES(JSON.stringify([userId, password])), privateKey),
    password: await encryptByPublicKeyOfRSA(publicKeyOfRSA, userId),
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

  const { data: user } = await axios.post<UserModel>(`/sign_in/get_account`, null, { params: { userId: userIdOrEmail } });
  const { privateKey, publicKey } = await generateKeyPairOfRSA();
  let privateKeyOfRSAOfUser: string;
  try {
    privateKeyOfRSAOfUser = await decryptByAES(await generateSecretKeyOfAES(JSON.stringify([user.id, password])), user.privateKeyOfRSA);
  } catch (error) {
    throw new Error('Incorrect password');
  }

  const { data: accessToken } = await axios.post<string>(`/sign_in`, null, {
    params: {
      userId: user.id,
      password: await encryptByPrivateKeyOfRSA(privateKeyOfRSAOfUser, JSON.stringify(new Date())),
      privateKeyOfRSA: await encryptByPublicKeyOfRSA(publicKey, privateKeyOfRSAOfUser),
    }
  });

  await setPrivateKeyOfRSA(privateKey);
  await setAccessToken(accessToken);
  await isSignIn();
}

export async function getUserInfo() {
  return axios.get<UserModel>("/get_user_info");
}

export async function signOut() {
  if (getAccessToken()) {
    await axios.post("/sign_out");
  }
  await removeAccessToken();
  await removePrivateKeyOfRSA();
  await removeUserIdOfGlobalUserInfo();
}

export async function isSignIn() {
  if (!getAccessToken()) {
    return false;
  }

  const { data: isSignIn } = await axios.get("/is_sign_in");
  if (isSignIn && !GlobalUserInfo.id) {
    const { data: userInfo } = await getUserInfo();
    const privateKeyOfRSAOfLocal = getPrivateKeyOfRSA();
    const privateKeyOfRSAOfUser = await decryptByPrivateKeyOfRSA(privateKeyOfRSAOfLocal, userInfo.privateKeyOfRSA);
    const publicKeyOfRSAOfUser = userInfo.publicKeyOfRSA;
    userInfo.encryptByPublicKeyOfRSA = async (data: string) => {
      return await encryptByPublicKeyOfRSA(publicKeyOfRSAOfUser, data);
    };
    userInfo.decryptByPrivateKeyOfRSA = async (data: string) => {
      return await decryptByPrivateKeyOfRSA(privateKeyOfRSAOfUser, data);
    };
    userInfo.encryptByPrivateKeyOfRSA = async (data: string) => {
      return await encryptByPrivateKeyOfRSA(privateKeyOfRSAOfUser, data);
    };
    userInfo.decryptByPublicKeyOfRSA = async (data: string) => {
      return await decryptByPublicKeyOfRSA(publicKeyOfRSAOfUser!, data);
    };
    setGlobalUserInfo(userInfo);
  }
  return isSignIn;
}