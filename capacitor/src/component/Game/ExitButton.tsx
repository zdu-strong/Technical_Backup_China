import { Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import ExitDialog from "@/component/Game/ExitDialog";
import { useMount, useUnmount } from "mobx-react-use-autorun";
import { AndroidNotch } from '@awesome-cordova-plugins/android-notch'
import { Capacitor } from '@capacitor/core'
import { delay, distinctUntilChanged, from, of, repeat, Subscription, tap } from "rxjs";
import { exhaustMapWithTrailing } from "rxjs-exhaustmap-with-trailing";
import SettingsIcon from '@mui/icons-material/Settings';

export default observer((props: { exit: () => void }) => {
  const state = useMobxState({
    exitDialog: {
      open: false,
    },
    ready: false,
    isLeftAndNotIsRight: false,
    leftOrRight: 10,
    subscription: new Subscription(),
    css: stylesheet({
      container: {
        width: "100%",
        height: "0px",
        position: "relative",
      },
      exitButton: {
        position: "absolute",
        top: "10px"
      }
    }),
  }, {
    ...props,
  });

  useMount(async () => {
    await loadSafeAreaInsets();
    await refreshSafeAreaInsets();
    state.ready = true;
  })

  async function loadSafeAreaInsets() {
    if (Capacitor.getPlatform() === "web") {
      return;
    }

    if (Capacitor.getPlatform() === "android") {
      const insetLeftPromise = AndroidNotch.getInsetLeft();
      const insetRightPromise = AndroidNotch.getInsetRight();
      const insetLeft = await insetLeftPromise;
      const insetRight = await insetRightPromise;
      state.isLeftAndNotIsRight = insetRight >= insetLeft;
    }
  }

  async function refreshSafeAreaInsets() {
    if (Capacitor.getPlatform() === "web") {
      return;
    }

    state.subscription.add(of(null).pipe(
      exhaustMapWithTrailing(() => from((async () => {
        if (Capacitor.getPlatform() === "android") {
          const insetLeftPromise = AndroidNotch.getInsetLeft();
          const insetRightPromise = AndroidNotch.getInsetRight();
          const insetLeft = await insetLeftPromise;
          const insetRight = await insetRightPromise;
          const isLeftAndNotIsRight = insetRight >= insetLeft;
          return isLeftAndNotIsRight;
        }
        return false;
      })())),
      distinctUntilChanged(),
      tap((isLeftAndNotIsRight) => {
        state.isLeftAndNotIsRight = isLeftAndNotIsRight;
      }),
      delay(100),
      repeat(),
    ).subscribe());
  }

  useUnmount(() => {
    state.subscription.unsubscribe();
  })

  return <>
    <div className={state.css.container}>
      {state.ready && <Fab
        size="small"
        color="primary"
        aria-label="add"
        style={state.isLeftAndNotIsRight ? { left: `${state.leftOrRight}px` } : { right: `${state.leftOrRight}px` }}
        className={state.css.exitButton}
        onClick={() => {
          state.exitDialog.open = true
        }}>
        <SettingsIcon />
      </Fab>}
    </div>
    {state.exitDialog.open && <ExitDialog exit={state.exit} closeDialog={() => { state.exitDialog.open = false }} />}
  </>
})