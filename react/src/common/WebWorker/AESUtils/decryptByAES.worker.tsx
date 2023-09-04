import registerWebworker from 'webworker-promise/lib/register'
import CryptoJS from 'crypto-js';

registerWebworker(async ({
  secretKeyOfAES,
  data
}: {
  secretKeyOfAES: string,
  data: string
}) => {
  const text = CryptoJS.AES.decrypt(
    data,
    CryptoJS.enc.Base64.parse(secretKeyOfAES),
    {
      iv: CryptoJS.MD5(CryptoJS.enc.Base64.parse(secretKeyOfAES)),
      padding: CryptoJS.pad.Pkcs7,
      mode: CryptoJS.mode.CBC,
    }
  ).toString(CryptoJS.enc.Utf8);
  return text;
});
