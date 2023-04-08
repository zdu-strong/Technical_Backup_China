import { Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import ExitDialog from "@/component/Game/ExitDialog";
import { useMount, useUnmount } from "mobx-react-use-autorun";
import { Subscription } from "rxjs";
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
    state.ready = true;
  })

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