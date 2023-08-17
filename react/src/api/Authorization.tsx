import { UserModel } from "@/model/UserModel";
import axios from "axios";
import { UserEmailModel } from "@/model/UserEmailModel";
import { GlobalUserInfo, removeGlobalUserInfo, setGlobalUserInfo } from "@/common/axios-config/AxiosConfig";
import { encryptByPublicKeyOfRSA, encryptByPrivateKeyOfRSA, generateKeyPairOfRSA } from "@/common/RSAUtils";
import { decryptByAES, encryptByAES, generateSecretKeyOfAES } from '@/common/AESUtils';
import { EMPTY, concat, concatMap, interval, lastValueFrom, of, take } from "rxjs";

export async function createNewAccountOfSignUp() {
  return await axios.post<UserModel>("/sign_up/create_new_account");
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
  return await axios.post("/sign_up/send_verification_code", {
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
  await setGlobalUserInfo(accessToken, privateKey);
}

export async function signOut() {
  await setGlobalUserInfo();
  if (GlobalUserInfo.accessToken) {
    try {
      await axios.post("/sign_out");
    } catch (e) {
      // do nothing
    }
  }
  await removeGlobalUserInfo();
}

export async function isSignIn() {
  await lastValueFrom(concat(of(null), interval(100)).pipe(
    concatMap(() => {
      if (GlobalUserInfo.loading) {
        return EMPTY;
      } else {
        return of(null);
      }
    }),
    take(1),
  ));
  return !!GlobalUserInfo.accessToken;
}