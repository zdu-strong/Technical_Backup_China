import { runWoker } from '../WebWorkerUtils';

export async function generateKeyPairOfRSA(): Promise<{ privateKey: string, publicKey: string }> {
  return await runWoker(new URL('../../common/WebWorker/RSAUtils/generateKeyPairOfRSA.worker', import.meta.url));
}

export async function encryptByPublicKeyOfRSA(publicKeyOfRSA: string, data: string): Promise<string> {
  return await runWoker(new URL('../../common/WebWorker/RSAUtils/encryptByPublicKeyOfRSA.worker', import.meta.url),
    {
      publicKeyOfRSA,
      data
    }
  );
}

export async function decryptByPrivateKeyOfRSA(privateKeyOfRSA: string, data: string): Promise<string> {
  return await runWoker(new URL('../../common/WebWorker/RSAUtils/decryptByPrivateKeyOfRSA.worker', import.meta.url),
    {
      privateKeyOfRSA,
      data
    }
  );
}

export async function encryptByPrivateKeyOfRSA(privateKeyOfRSA: string, data: string): Promise<string> {
  return await runWoker(new URL('../../common/WebWorker/RSAUtils/encryptByPrivateKeyOfRSA.worker', import.meta.url),
    {
      privateKeyOfRSA,
      data
    }
  );
}

export async function decryptByPublicKeyOfRSA(publicKeyOfRSA: string, data: string): Promise<string> {
  return await runWoker(new URL('../../common/WebWorker/RSAUtils/decryptByPublicKeyOfRSA.worker', import.meta.url),
    {
      publicKeyOfRSA,
      data
    }
  );
}
