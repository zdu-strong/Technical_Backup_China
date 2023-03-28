import WebworkerPromise from 'webworker-promise'

export async function generateSecretKeyOfAES(password?: string) {
  return await new WebworkerPromise(new Worker(new URL('../../common/WebWorker/AESUtils/generateSecretKeyOfAES.worker', import.meta.url), { type: "module" })).postMessage({
    password,
  });
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