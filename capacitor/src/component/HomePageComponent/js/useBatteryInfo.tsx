import { BatteryInfo, Device } from '@capacitor/device';
import { useMobxState } from 'mobx-react-use-autorun';
import { useMount } from "mobx-react-use-autorun"
import { concatMap, delay, from, of, repeat, Subscription, tap } from 'rxjs';

export const useBatteryInfo = () => {
  const state = useMobxState({
    batteryInfo: null as BatteryInfo | null,
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

  useMount((subscription) => {
    loadBatteryInfo(subscription);
  })

  return state.batteryInfo;
}