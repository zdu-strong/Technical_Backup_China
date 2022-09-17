import axios from 'axios';
import qs from 'qs';
import { ServerAddress } from '@/common/Server'
import { UserModel } from '@/model/UserModel';
import { observable } from 'mobx-react-use-autorun';
import { concat, fromEvent, of, retry, tap } from 'rxjs';

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
    const accessToken = getAccessToken();
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
} as UserModel);

export function setGlobalUserInfo(userInfo: UserModel): void {
  GlobalUserInfo.id = userInfo.id;
  GlobalUserInfo.username = userInfo.username;
  GlobalUserInfo.encryptByPublicKeyOfRSA = userInfo.encryptByPublicKeyOfRSA;
  GlobalUserInfo.decryptByPrivateKeyOfRSA = userInfo.decryptByPrivateKeyOfRSA;
  GlobalUserInfo.encryptByPrivateKeyOfRSA = userInfo.encryptByPrivateKeyOfRSA;
  GlobalUserInfo.decryptByPublicKeyOfRSA = userInfo.decryptByPublicKeyOfRSA;
}

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

const keyOfAccessTokenOfLoalStorage = 'token-c12e6be9-e969-4a54-b5d4-b451755bf49a';

export async function setAccessToken(accessToken: string) {
  GlobalUserInfo.accessToken = accessToken;
  window.localStorage.setItem(keyOfAccessTokenOfLoalStorage, accessToken);
}

export async function removeAccessToken() {
  GlobalUserInfo.accessToken = '';
  window.localStorage.removeItem(keyOfAccessTokenOfLoalStorage);
}

const keyOfPrivateKeyOfAccessTokenOfRSAOfLocalStorage = 'privateKeyOfRSA-e3e5a91b-7c4a-4f15-afd7-3917bbc718ba';

export async function setPrivateKeyOfRSA(privateKeyOfRSA: string) {
  GlobalUserInfo.privateKeyOfRSAOfAccessToken = privateKeyOfRSA;
  window.localStorage.setItem(keyOfPrivateKeyOfAccessTokenOfRSAOfLocalStorage, privateKeyOfRSA);
}

export async function removePrivateKeyOfRSA() {
  GlobalUserInfo.privateKeyOfRSAOfAccessToken = '';
  window.localStorage.removeItem(keyOfPrivateKeyOfAccessTokenOfRSAOfLocalStorage);
}

if (existWindow) {
  concat(of(null), fromEvent(window, "storage")).pipe(
    tap(() => {
      if (getAccessToken() && !window.localStorage.getItem(keyOfAccessTokenOfLoalStorage)) {
        GlobalUserInfo.accessToken = '';
      } else if (window.localStorage.getItem(keyOfAccessTokenOfLoalStorage) && window.localStorage.getItem(keyOfAccessTokenOfLoalStorage) !== GlobalUserInfo.accessToken) {
        GlobalUserInfo.accessToken = window.localStorage.getItem(keyOfAccessTokenOfLoalStorage)!;
      }
      if (getPrivateKeyOfRSA() && !window.localStorage.getItem(keyOfPrivateKeyOfAccessTokenOfRSAOfLocalStorage)) {
        GlobalUserInfo.privateKeyOfRSAOfAccessToken = '';
      } else if (window.localStorage.getItem(keyOfPrivateKeyOfAccessTokenOfRSAOfLocalStorage) && window.localStorage.getItem(keyOfPrivateKeyOfAccessTokenOfRSAOfLocalStorage) !== getPrivateKeyOfRSA()) {
        GlobalUserInfo.privateKeyOfRSAOfAccessToken = window.localStorage.getItem(keyOfPrivateKeyOfAccessTokenOfRSAOfLocalStorage)!;
      }
    }),
    retry(),
  ).subscribe();
}

export function getPrivateKeyOfRSA() {
  return GlobalUserInfo.privateKeyOfRSAOfAccessToken;
}

export function getAccessToken(): string {
  return GlobalUserInfo.accessToken!;
}
