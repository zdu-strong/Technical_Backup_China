import { Database } from "@/common/database";
import linq from 'linq'

export async function getUser() {
  const db = new Database();
  const stream = linq.from(await db.UserList.toArray());
  return stream.first();
}