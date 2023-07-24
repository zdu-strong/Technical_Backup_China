import { BatteryInfo, Device } from '@capacitor/device';
import { useMobxState } from 'mobx-react-use-autorun';
import { useMount } from "mobx-react-use-autorun"
import { concatMap, delay, from, of, repeat, Subscription, tap } from 'rxjs';

export function useBatteryInfo() {

  const state = useMobxState({
    batteryInfo: null as BatteryInfo | null,
  })

  useMount((subscription) => {
    loadBatteryInfo(subscription);
  })

  function loadBatteryInfo(subscription: Subscription) {
    subscription.add(of(null).pipe(
      concatMap(() => from(Device.getBatteryInfo())),
      tap((batteryInfo) => {
        state.batteryInfo = batteryInfo;
      }),
      delay(100),
      repeat(),
    ).subscribe());
  }

  return state.batteryInfo;
}