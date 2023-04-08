import axios from "axios";
import { timer } from "rxjs";
import { ServerAddress } from "@/common/Server";
import { concatMap, from, map, range, toArray } from "rxjs";
import * as mathjs from 'mathjs'
import { getLongTermTask } from '@/api/LongTermTask'

export async function upload(file: File) {
  for (let i = 10; i > 0; i--) {
    await timer(1).toPromise();
  }
  /* Each piece is 10MB */
  const everySize = 1024 * 1024 * 10;
  const url: String | undefined = await range(1, mathjs.max(mathjs.ceil(mathjs.divide(file.size, everySize)), 1)).pipe(
    concatMap((pageNum) => {
      const formData = new FormData();
      formData.set("file", new File([file.slice((pageNum - 1) * everySize, pageNum * everySize)], file.name, file));
      return from(axios.post("/upload/resource", formData));
    }),
    map((response) => response.data),
    toArray(),
    concatMap(urlList => from(axios.post<string>("/upload/merge", urlList))),
    concatMap(response => from(getLongTermTask(response.data, String))),
  ).toPromise();
  return {
    url: `${ServerAddress}${url!}`,
    downloadUrl: `${ServerAddress}/download${url}`,
  };
}