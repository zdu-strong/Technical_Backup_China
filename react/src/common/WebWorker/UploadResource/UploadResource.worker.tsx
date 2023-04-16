import '@/common/axios-config/AxiosConfig'
import registerWebworker from 'webworker-promise/lib/register'
import axios from "axios";
import { catchError, concatMap, from, map, of, range, toArray } from "rxjs";
import * as mathjs from 'mathjs'
import { getLongTermTask } from '@/api/LongTermTask';

registerWebworker(async ({
  ServerAddress,
  file
}: {
  ServerAddress: string
  file: File
}) => {
  if (!ServerAddress) {
    throw new Error("Server Address cannot be empty");
  }
  /* Each piece is 10MB */
  const everySize = 1024 * 1024 * 10;
  const url: String | undefined = await range(1, mathjs.max(mathjs.ceil(mathjs.divide(file.size, everySize)), 1)).pipe(
    concatMap((pageNum) => {
      const formData = new FormData();
      formData.set("file", new File([file.slice((pageNum - 1) * everySize, pageNum * everySize)], file.name, file));
      return from(axios.post(`${ServerAddress}/upload/resource`, formData));
    }),
    map((response) => response.data),
    toArray(),
    concatMap(urlList => of(null).pipe(
      concatMap(() => from(axios.post<string>(`${ServerAddress}/upload/merge`, urlList))),
      concatMap(response => from(getLongTermTask(`${ServerAddress}${response.data}`, String))),
      catchError((error, caught) => {
        if (typeof error!.message === 'string' && error.message.includes("The task failed because it stopped")) {
          return caught;
        } else {
          throw error;
        }
      }),
    )),
  ).toPromise();
  return {
    url: `${ServerAddress}${url!}`,
    downloadUrl: `${ServerAddress}/download${url}`,
  };
});

