import WebworkerPromise from 'webworker-promise'
import { v1, v4 } from 'uuid'
import CryptoJS from 'crypto-js'

export async function generateSecretKeyOfAES() {
  var salt = CryptoJS.lib.WordArray.random(128 / 8);
  var key256Bits = CryptoJS.PBKDF2(JSON.stringify([v1(), v4()]), salt, {
    keySize: 256 / 32
  });
  return key256Bits.toString(CryptoJS.enc.Base64);
}

export async function encryptByAES(secretKeyOfAES: string, data: string): Promise<string> {
  return await new WebworkerPromise(new Worker(new URL('../../common/WebWorker/AESUtils/encryptByAES.worker', import.meta.url), { type: "module" })).postMessage({
    secretKeyOfAES,
    data
  });
}

export async function decryptByAES(secretKeyOfAES: string, data: string): Promise<string> {
  return await new WebworkerPromise(new Worker(new URL('../../common/WebWorker/AESUtils/decryptByAES.worker', import.meta.url), { type: "module" })).postMessage({
    secretKeyOfAES,
    data
  });
}