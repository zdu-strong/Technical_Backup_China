import { observer, useMobxState } from 'mobx-react-use-autorun';
import { Dialog } from '@mui/material';
import Game from '.';

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
    >
      <Game />
    </Dialog>
  </>
})