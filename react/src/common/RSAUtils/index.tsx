import WebworkerPromise from 'webworker-promise'

export async function generateKeyPairOfRSA(): Promise<{ privateKey: string, publicKey: string }> {
  return await new WebworkerPromise(new Worker(new URL('../../common/WebWorker/RSAUtils/generateKeyPairOfRSA.worker', import.meta.url), { type: "module" })).postMessage({});
}

export async function encryptByPublicKeyOfRSA(publicKeyOfRSA: string, data: string): Promise<string> {
  return await new WebworkerPromise(new Worker(new URL('../../common/WebWorker/RSAUtils/encryptByPublicKeyOfRSA.worker', import.meta.url), { type: "module" })).postMessage({
    publicKeyOfRSA,
    data
  });
}

export async function decryptByPrivateKeyOfRSA(privateKeyOfRSA: string, data: string): Promise<string> {
  return await new WebworkerPromise(new Worker(new URL('../../common/WebWorker/RSAUtils/decryptByPrivateKeyOfRSA.worker', import.meta.url), { type: "module" })).postMessage({
    privateKeyOfRSA,
    data
  });
}

export async function encryptByPrivateKeyOfRSA(privateKeyOfRSA: string, data: string): Promise<string> {
  return await new WebworkerPromise(new Worker(new URL('../../common/WebWorker/RSAUtils/encryptByPrivateKeyOfRSA.worker', import.meta.url), { type: "module" })).postMessage({
    privateKeyOfRSA,
    data
  });
}

export async function decryptByPublicKeyOfRSA(publicKeyOfRSA: string, data: string): Promise<string> {
  return await new WebworkerPromise(new Worker(new URL('../../common/WebWorker/RSAUtils/decryptByPublicKeyOfRSA.worker', import.meta.url), { type: "module" })).postMessage({
    publicKeyOfRSA,
    data
  });
}
