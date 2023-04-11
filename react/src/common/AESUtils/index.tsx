import { runWoker } from '../WebWorkerUtils';

export async function generateSecretKeyOfAES(password?: string) {
  return await runWoker(new URL('../../common/WebWorker/AESUtils/generateSecretKeyOfAES.worker', import.meta.url),
    {
      password
    }
  );
}

export async function encryptByAES(secretKeyOfAES: string, data: string): Promise<string> {
  return await runWoker(new URL('../../common/WebWorker/AESUtils/encryptByAES.worker', import.meta.url),
    {
      secretKeyOfAES,
      data
    }
  );
}

export async function decryptByAES(secretKeyOfAES: string, data: string): Promise<string> {
  return await runWoker(new URL('../../common/WebWorker/AESUtils/decryptByAES.worker', import.meta.url),
    {
      secretKeyOfAES,
      data
    }
  );
}