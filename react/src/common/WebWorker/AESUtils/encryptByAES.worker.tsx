import registerWebworker from 'webworker-promise/lib/register'
import CryptoJS from 'crypto-js';

registerWebworker(async ({
  secretKeyOfAES,
  data
}: {
  secretKeyOfAES: string,
  data: string
}) => {
  return CryptoJS.AES.encrypt(
    CryptoJS.enc.Utf8.parse(data),
    CryptoJS.enc.Base64.parse(secretKeyOfAES),
    {
      iv: CryptoJS.MD5(CryptoJS.enc.Base64.parse(secretKeyOfAES)),
      padding: CryptoJS.pad.Pkcs7,
      mode: CryptoJS.mode.CBC,
    }
  ).toString();
});
