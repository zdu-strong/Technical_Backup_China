import registerWebworker from 'webworker-promise/lib/register'
import axios from 'axios'
import { UserModel } from "@/model/UserModel";

registerWebworker(async ({
  ServerAddress,
  accessToken
}: {
  ServerAddress: string;
  accessToken: string;
}) => {
  const { data: userInfo } = await axios.get<UserModel>(`${ServerAddress}/get_user_info`, { headers: { "Authorization": 'Bearer ' + accessToken } });
  return userInfo;
});
