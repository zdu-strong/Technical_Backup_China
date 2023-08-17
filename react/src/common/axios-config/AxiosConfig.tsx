import axios from 'axios';
import qs from 'qs';
import { ServerAddress } from '@/common/Server'
import { UserModel } from '@/model/UserModel';
import { observable } from 'mobx-react-use-autorun';
import { catchError, concat, from, fromEvent, of, retry, switchMap, tap } from 'rxjs';
import { decryptByPrivateKeyOfRSA, decryptByPublicKeyOfRSA, encryptByPrivateKeyOfRSA, encryptByPublicKeyOfRSA } from '../RSAUtils';
import { TypedJSON } from 'typedjson';
import { runWoker } from '../WebWorkerUtils';

axios.defaults.baseURL = ServerAddress;

axios.defaults.paramsSerializer = {
  serialize(params: Record<string, any>) {
    return qs.stringify(
      params,
      {
        arrayFormat: 'repeat',
      }
    );
  }
}

axios.interceptors.response.use(undefined, async (error) => {
  if (typeof error?.response?.data === "object") {
    for (const objectKey in error.response.data) {
      error[objectKey] = error.response.data[objectKey];
    }
  }

  throw error;
});

axios.interceptors.request.use((config) => {
  if (config.url?.startsWith("/") || config.url?.startsWith(ServerAddress + "/") || config.url === ServerAddress) {
    const accessToken = GlobalUserInfo.accessToken;
    if (accessToken) {
      config.headers!["Authorization"] = 'Bearer ' + accessToken
    }
  }
  return config;
})

export const GlobalUserInfo = observable({
  id: '',
  username: '',
  accessToken: '',
  privateKeyOfRSAOfAccessToken: '',
  loading: true,
} as UserModel);

export async function setGlobalUserInfo(accessToken?: string, privateKeyOfRSAOfAccessToken?: string): Promise<void> {
  if (!accessToken || !privateKeyOfRSAOfAccessToken) {
    const jsonStringOfLocalStorage = window.localStorage.getItem(keyOfGlobalUserInfoOfLocalStorage);
    if (jsonStringOfLocalStorage) {
      const jsonOfLocalStorage = new TypedJSON(UserModel).parse(jsonStringOfLocalStorage);
      accessToken = jsonOfLocalStorage?.accessToken;
      privateKeyOfRSAOfAccessToken = jsonOfLocalStorage?.privateKeyOfRSAOfAccessToken;
      if (GlobalUserInfo.accessToken === accessToken && GlobalUserInfo.privateKeyOfRSAOfAccessToken === privateKeyOfRSAOfAccessToken) {
        return;
      }
    } else {
      if (GlobalUserInfo.accessToken) {
        await removeGlobalUserInfo();
      }
      return;
    }
  }

  if (!accessToken || !privateKeyOfRSAOfAccessToken) {
    return;
  }

  const userInfo = await getUserInfo(accessToken!);
  const privateKeyOfRSAOfUser = await decryptByPrivateKeyOfRSA(privateKeyOfRSAOfAccessToken!, userInfo.privateKeyOfRSA);
  const publicKeyOfRSAOfUser = userInfo.publicKeyOfRSA;
  GlobalUserInfo.id = userInfo.id;
  GlobalUserInfo.username = userInfo.username;
  GlobalUserInfo.accessToken = accessToken;
  GlobalUserInfo.privateKeyOfRSAOfAccessToken = privateKeyOfRSAOfAccessToken;
  GlobalUserInfo.encryptByPublicKeyOfRSA = async (data: string) => {
    return await encryptByPublicKeyOfRSA(publicKeyOfRSAOfUser, data);
  };
  GlobalUserInfo.decryptByPrivateKeyOfRSA = async (data: string) => {
    return await decryptByPrivateKeyOfRSA(privateKeyOfRSAOfUser, data);
  };
  GlobalUserInfo.encryptByPrivateKeyOfRSA = async (data: string) => {
    return await encryptByPrivateKeyOfRSA(privateKeyOfRSAOfUser, data);
  };
  GlobalUserInfo.decryptByPublicKeyOfRSA = async (data: string) => {
    return await decryptByPublicKeyOfRSA(publicKeyOfRSAOfUser!, data);
  };
  window.localStorage.setItem(keyOfGlobalUserInfoOfLocalStorage, JSON.stringify({
    accessToken,
    privateKeyOfRSAOfAccessToken
  }))
}

export async function removeGlobalUserInfo() {
  GlobalUserInfo.id = '';
  GlobalUserInfo.username = '';
  GlobalUserInfo.accessToken = '';
  GlobalUserInfo.privateKeyOfRSAOfAccessToken = '';
  GlobalUserInfo.encryptByPublicKeyOfRSA = undefined as any;
  GlobalUserInfo.decryptByPrivateKeyOfRSA = undefined as any;
  GlobalUserInfo.encryptByPrivateKeyOfRSA = undefined as any;
  GlobalUserInfo.decryptByPublicKeyOfRSA = undefined as any;
  if (window.localStorage.getItem(keyOfGlobalUserInfoOfLocalStorage)) {
    window.localStorage.removeItem(keyOfGlobalUserInfoOfLocalStorage);
  }
}

const keyOfGlobalUserInfoOfLocalStorage = 'GlobalUserInfo-c12e6be9-e969-4a54-b5d4-b451755bf49a';

const existWindow = (() => {
  try {
    if (window) {
      return true;
    } else {
      return false;
    }
  } catch {
    return false;
  }
})();

function main() {
  if (existWindow) {
    concat(of(null), fromEvent(window, "storage")).pipe(
      switchMap(() => {
        return from(setGlobalUserInfo()).pipe(
          catchError(() => of(null))
        );
      }),
      tap(() => {
        GlobalUserInfo.loading = false;
      }),
      retry(),
    ).subscribe();
  }
}

async function getUserInfo(accessToken: string) {
  const userInfo = await runWoker(new Worker(new URL('../../common/WebWorker/GetUserInfo/getUserInfo.worker', import.meta.url), { type: "module" }),
    {
      ServerAddress,
      accessToken
    }
  );
  return new TypedJSON(UserModel).parse(userInfo)!;
}

export default main()
