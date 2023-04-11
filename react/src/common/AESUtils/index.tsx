import { runWoker } from '@/common/WebWorkerUtils';

export async function generateSecretKeyOfAES(password?: string) {
  return await runWoker(new Worker(new URL('../../common/WebWorker/AESUtils/generateSecretKeyOfAES.worker', import.meta.url), { type: "module" }),
    {
      password
    }
  );
}

export async function encryptByAES(secretKeyOfAES: string, data: string): Promise<string> {
  return await runWoker(new Worker(new URL('../../common/WebWorker/AESUtils/encryptByAES.worker', import.meta.url), { type: "module" }),
    {
      secretKeyOfAES,
      data
    }
  );
}

export async function decryptByAES(secretKeyOfAES: string, data: string): Promise<string> {
  return await runWoker(new Worker(new URL('../../common/WebWorker/AESUtils/decryptByAES.worker', import.meta.url), { type: "module" }),
    {
      secretKeyOfAES,
      data
    }
  );
}