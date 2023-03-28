import registerWebworker from 'webworker-promise/lib/register'
import CryptoJS from 'crypto-js';
import { v1, v4 } from 'uuid'

registerWebworker(async ({
  password,
}: {
  password?: string,
}) => {
  const salt = password ? CryptoJS.MD5(password) : CryptoJS.lib.WordArray.random(128 / 8);
  if (!password) {
    password = JSON.stringify([v1(), v4()]);
  }
  const key256Bits = CryptoJS.PBKDF2(password, salt, {
    keySize: 256 / 32,
    hasher: CryptoJS.algo.SHA256
  });
  return key256Bits.toString(CryptoJS.enc.Base64);
});
