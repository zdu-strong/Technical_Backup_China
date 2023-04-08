import { observer, useMobxState } from 'mobx-react-use-autorun';
import { Dialog } from '@mui/material';
import Game from '.';
import ExitButton from './ExitButton';

export default observer((props: {
  closeDialog: () => void
}) => {

  const state = useMobxState({}, {
    ...props
  })

  return <>
    <Dialog
      fullScreen
      open={true}
      onClose={state.closeDialog}
      disableRestoreFocus={true}
      disableEscapeKeyDown={true}
    >
      <ExitButton exit={state.closeDialog} />
      <Game />
    </Dialog>
  </>
})