import { timer } from "rxjs";
import { ServerAddress } from "@/common/Server";
import { runWoker } from '@/common/WebWorkerUtils';

export async function upload(file: File): Promise<{ url: string, downloadUrl: string }> {
  for (let i = 10; i > 0; i--) {
    await timer(1).toPromise();
  }
  return await runWoker(new Worker(new URL('../common/WebWorker/UploadResource/UploadResource.worker', import.meta.url), { type: "module" }),
    {
      ServerAddress,
      file: file
    }
  );
}