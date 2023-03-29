import { useMobxState } from "mobx-react-use-autorun";
import { useMount, useUnmount } from "mobx-react-use-autorun"
import { concatMap, from, of, repeat, Subscription, tap } from "rxjs";
import remote from "@/remote";

export const useCpuUsage = () => {
  const state = useMobxState({
    cpuUsage: null as number | null,
    subscription: new Subscription(),
  })

  function loadCpuUsage() {
    state.subscription.add(of(null).pipe(
      concatMap(() => from(remote.NodeOsUtils.cpu.usage())),
      tap((cpuUsage) => {
        state.cpuUsage = cpuUsage;
      }),
      repeat({ delay: 100 }),
    ).subscribe());
  }

  useMount(() => {
    loadCpuUsage()
  })

  useUnmount(() => {
    state.subscription.unsubscribe();
  })

  return state.cpuUsage;
}