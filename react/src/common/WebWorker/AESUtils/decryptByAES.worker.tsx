import registerWebworker from 'webworker-promise/lib/register'
import CryptoJS from 'crypto-js';

registerWebworker(async ({
  secretKeyOfAES,
  data
}: {
  secretKeyOfAES: string,
  data: string
}) => {
  const text = CryptoJS.AES.decrypt(data, secretKeyOfAES).toString(CryptoJS.enc.Utf8);
  if (text === "") {
    throw new Error("Malformed UTF-8 data");
  }
  return text;
});
