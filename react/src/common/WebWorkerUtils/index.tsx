import { ReplaySubject } from "rxjs";
import WebworkerPromise from 'webworker-promise'

export async function runWoker(worker: Worker, params?: any) {
  const workerSubject = new ReplaySubject();
  worker.onerror = () => {
    workerSubject.error(new Error("Webworker file load failed!"));
  };
  return await Promise.race([new WebworkerPromise(worker).postMessage(params || {}), workerSubject.toPromise()]);
}