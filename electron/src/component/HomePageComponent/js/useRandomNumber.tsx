import { useMobxState } from "mobx-react-use-autorun";
import { useMount, useUnmount } from "mobx-react-use-autorun"
import { concatMap, EMPTY, interval, of, repeat, Subscription, take, tap } from "rxjs";

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
            return of(randomNumber);
          } else {
            return EMPTY;
          }
        }),
        take(1),
      )),
      tap((randomNumber) => {
        state.randomNumber = randomNumber;
      }),
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