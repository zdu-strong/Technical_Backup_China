import { getDatabase } from "@/common/database";
import linq from 'linq'

export async function getUser() {
  const { UserList } = await getDatabase();
  const user = linq.from(UserList).first();
  return user;
}