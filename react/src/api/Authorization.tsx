import { UserModel } from "@/model/UserModel";
import axios from "axios";
import { UserEmailModel } from "@/model/UserEmailModel";
import { GlobalUserInfo, removeGlobalUserInfo, setGlobalUserInfo } from "@/common/axios-config/AxiosConfig";
import { decryptByPrivateKeyOfRSA, encryptByPrivateKeyOfRSA, encryptByPublicKeyOfRSA, generateKeyPairOfRSA } from "@/common/RSAUtils";
import { decryptByAES, encryptByAES, generateSecretKeyOfAES } from '@/common/AESUtils';
import { EMPTY, concat, concatMap, interval, lastValueFrom, of, take } from "rxjs";
import { VerificationCodeEmailModel } from "@/model/VerificationCodeEmailModel";

export async function signUp(password: string, nickname: string, userEmailList: UserEmailModel[]): Promise<void> {
  const { privateKey, publicKey } = await generateKeyPairOfRSA();
  const keyPairOfRSAForPassword = await generateKeyPairOfRSA();
  var { data: user } = await axios.post<UserModel>(`/sign_up`, {
    username: nickname,
    userEmailList: userEmailList,
    publicKeyOfRSA: publicKey,
    privateKeyOfRSA: await encryptByPublicKeyOfRSA(keyPairOfRSAForPassword.publicKey, privateKey),
    password: Buffer.from(JSON.stringify([await encryptByAES(await generateSecretKeyOfAES(password), keyPairOfRSAForPassword.privateKey), keyPairOfRSAForPassword.publicKey]), "utf8").toString("base64"),
  });
  await signIn(user.id, password);
}

export async function sendVerificationCode(email: string) {
  return await axios.post<VerificationCodeEmailModel>("/email/send_verification_code", null, { params: { email } });
}

export async function signIn(userIdOrEmail: string, password: string): Promise<void> {
  await signOut();

  const { data: user } = await axios.post<UserModel>(`/sign_in/get_account`, null, { params: { userId: userIdOrEmail } });
  const secretKeyOfAESOfAccessToken = await generateSecretKeyOfAES();
  let privateKeyOfRSA: string;
  try {
    privateKeyOfRSA = await decryptByPrivateKeyOfRSA(await decryptByAES(await generateSecretKeyOfAES(password), user.password), user.privateKeyOfRSA);
  } catch (error) {
    throw new Error('Incorrect password');
  }

  const { data: accessToken } = await axios.post<string>(`/sign_in`, null, {
    params: {
      userId: user.id,
      password: await encryptByPrivateKeyOfRSA(await decryptByAES(await generateSecretKeyOfAES(password), user.password), JSON.stringify({
        createDate: new Date(),
        privateKeyOfRSA: await encryptByAES(secretKeyOfAESOfAccessToken, privateKeyOfRSA),
      })),
    }
  });
  await setGlobalUserInfo(accessToken, secretKeyOfAESOfAccessToken);
}

export async function signOut() {
  if (await isSignIn()) {
    try {
      await axios.post("/sign_out");
    } catch {
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