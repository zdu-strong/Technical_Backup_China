import { ReplaySubject } from "rxjs";
import WebworkerPromise from 'webworker-promise'

export async function runWoker(url: URL, params?: any) {
  const worker = new Worker(url, { type: "module" });
  const workerSubject = new ReplaySubject();
  worker.onerror = () => {
    workerSubject.error(new Error("Webworker file load failed!"));
  };
  return await Promise.race([new WebworkerPromise(worker).postMessage(params || {}), workerSubject.toPromise()]);
}