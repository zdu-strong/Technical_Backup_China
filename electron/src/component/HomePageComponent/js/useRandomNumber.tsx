import { useMobxState } from "mobx-react-use-autorun";
import { useMount, useUnmount } from "mobx-react-use-autorun"
import { concatMap, EMPTY, interval, of, repeat, Subscription, take } from "rxjs";

export function useRandomNumber() {
  const state = useMobxState({
    randomNumber: 16,
    subscription: new Subscription(),
  })

  function loadRandomNumber() {
    state.subscription.add(of(null).pipe(
      concatMap(() => interval(1).pipe(
        concatMap(() => {
          const randomNumber = Math.floor(Math.random() * 100 + 1);
          if (state.randomNumber !== randomNumber) {
            state.randomNumber = randomNumber;
            return of(null);
          } else {
            return EMPTY;
          }
        }),
        take(1),
      )),
      repeat({ delay: 1000 }),
    ).subscribe());
  }

  useMount(() => {
    loadRandomNumber()
  })

  useUnmount(() => {
    state.subscription.unsubscribe();
  })

  return state.randomNumber;
}