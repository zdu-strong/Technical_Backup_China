import { observer, useMobxState } from 'mobx-react-use-autorun';
import { Dialog } from '@mui/material';
import Game from './Game';
import ExitButton from '@/component/Game/ExitButton';
import { useRef } from 'react';

export default observer((props: {
  closeDialog: () => void
}) => {

  const state = useMobxState({}, {
    ...props,
    canvasRef: useRef<HTMLCanvasElement>(),
  })

  return <>
    <Dialog
      fullScreen
      open={true}
      onClose={state.closeDialog}
      disableRestoreFocus={true}
      disableEscapeKeyDown={true}
    >
      <ExitButton
        exit={state.closeDialog}
        canvasRef={state.canvasRef}
      />
      <Game
        canvasRef={state.canvasRef}
      />
    </Dialog>
  </>
})