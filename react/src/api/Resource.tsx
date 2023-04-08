import { timer } from "rxjs";
import { ServerAddress } from "@/common/Server";
import WebworkerPromise from 'webworker-promise'

export async function upload(file: File) {
  for (let i = 10; i > 0; i--) {
    await timer(1).toPromise();
  }
  const { url, downloadUrl }: { url: string, downloadUrl: string } = await new WebworkerPromise(new Worker(new URL('../common/WebWorker/UploadResource/UploadResource.worker', import.meta.url), { type: "module" })).postMessage({
    ServerAddress,
    file: file
  });
  return { url, downloadUrl };
}