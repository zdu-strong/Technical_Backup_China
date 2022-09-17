import { decryptByPublicKeyOfRSA, encryptByPublicKeyOfRSA } from "@/common/RSAUtils";
import { UserModel } from "@/model/UserModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function getUserById(userId: string) {
  const response = await axios.get<UserModel>("/get_user_by_id", { params: { userId: userId } });
  response.data = new TypedJSON(UserModel).parse(response.data) as any;
  response.data.encryptByPublicKeyOfRSA = async (data: string) => {
    return await encryptByPublicKeyOfRSA(response.data!.publicKeyOfRSA, data);
  }
  response.data.decryptByPublicKeyOfRSA = async (data: string) => {
    return await decryptByPublicKeyOfRSA(response.data!.publicKeyOfRSA, data);
  }
  return response;
}
