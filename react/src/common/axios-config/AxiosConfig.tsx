import axios from 'axios';
import qs from 'qs';
import { ServerAddress } from '@/common/Server'
import { UserModel } from '@/model/UserModel';
import { observable } from 'mobx-react-use-autorun';

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

const tokenKeyOfLoalStorage = 'token-c12e6be9-e969-4a54-b5d4-b451755bf49a';

export function setAccessToken(accessToken: string) {
  localStorage.setItem(tokenKeyOfLoalStorage, accessToken);
}

export function removeAccessToken() {
  localStorage.removeItem(tokenKeyOfLoalStorage);
}

const keyOfPrivateKeyOfRSA = 'privateKeyOfRSA-e3e5a91b-7c4a-4f15-afd7-3917bbc718ba';

export function setPrivateKeyOfRSA(privateKeyOfRSA: string){
  localStorage.setItem(keyOfPrivateKeyOfRSA, privateKeyOfRSA);
}

export function removePrivateKeyOfRSA(){
  localStorage.removeItem(keyOfPrivateKeyOfRSA);
}

export function getPrivateKeyOfRSA(){
  return localStorage.getItem(keyOfPrivateKeyOfRSA);
}

export function getAccessToken(): string {
  const accessToken = localStorage.getItem(tokenKeyOfLoalStorage);
  if (accessToken) {
    return accessToken;
  } else {
    return '';
  }
}

export const GlobalUserInfo: UserModel = observable({});

export function setGlobalUserInfo(userInfo: UserModel): void {
  GlobalUserInfo.id = userInfo.id;
  GlobalUserInfo.username = userInfo.username;
}
