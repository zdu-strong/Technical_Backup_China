import { observer, useMobxState } from 'mobx-react-use-autorun';
import { Dialog } from '@mui/material';
import Game from '.';
import { useMount, useUnmount } from "mobx-react-use-autorun";
import { LANDSCAPE, PORTRAIT_PRIMARY } from '@/common/ScreenOrentation';
import ExitButton from '@/component/Game/ExitButton';

export default observer((props: {
  closeDialog: () => void
}) => {
  const state = useMobxState({
    ready: false,
  }, {
    ...props,
  })

  useMount(async () => {
    await LANDSCAPE();
    state.ready = true;
  })

  useUnmount(async () => {
    await PORTRAIT_PRIMARY()
  })

  return <>
    <Dialog
      fullScreen
      open={true}
      onClose={state.closeDialog}
      disableRestoreFocus={true}
    >
      {state.ready && <ExitButton exit={state.closeDialog} />}
      <Game />
    </Dialog>
  </>
})