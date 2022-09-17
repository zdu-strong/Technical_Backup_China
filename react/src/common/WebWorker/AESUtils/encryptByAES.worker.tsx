import registerWebworker from 'webworker-promise/lib/register'
import CryptoJS from 'crypto-js';

registerWebworker(async ({
  secretKeyOfAES,
  data
}: {
  secretKeyOfAES: string,
  data: string
}) => {
  if (!data) {
    throw new Error("Empty data is not allowed");
  }
  return CryptoJS.AES.encrypt(CryptoJS.enc.Utf8.parse(data), secretKeyOfAES).toString();
});
