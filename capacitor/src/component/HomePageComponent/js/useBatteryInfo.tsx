import { BatteryInfo, Device } from '@capacitor/device';
import { useMobxState } from 'mobx-react-use-autorun';
import { useMount, useUnmount } from 'react-use'
import { concatMap, delay, from, of, repeat, Subscription, tap } from 'rxjs';

export const useBatteryInfo = () => {
  const state = useMobxState({
    batteryInfo: null as BatteryInfo | null,
    subscription: new Subscription(),
  })

  async function loadBatteryInfo() {
    state.subscription.add(of(null).pipe(
      concatMap(() => from(Device.getBatteryInfo())),
      tap((batteryInfo) => {
        state.batteryInfo = batteryInfo;
      }),
      delay(100),
      repeat(),
    ).subscribe());
  }

  useMount(() => {
    loadBatteryInfo();
  })

  useUnmount(() => {
    state.subscription.unsubscribe()
  })

  return state.batteryInfo;
}