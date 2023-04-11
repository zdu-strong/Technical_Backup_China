import { timer } from "rxjs";
import { ServerAddress } from "@/common/Server";
import { runWoker } from "@/common/WebWorkerUtils";

export async function upload(file: File) {
  for (let i = 10; i > 0; i--) {
    await timer(1).toPromise();
  }
  const { url, downloadUrl }: { url: string, downloadUrl: string } = await runWoker(new URL('../common/WebWorker/UploadResource/UploadResource.worker', import.meta.url),
    {
      ServerAddress,
      file: file
    }
  );
  return { url, downloadUrl };
}