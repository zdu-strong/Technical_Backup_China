import { observer } from 'mobx-react-use-autorun';
import { Dialog } from '@mui/material';
import Game from '.';

export default observer((props: {
  closeDialog: () => void
}) => {

  return <>
    <Dialog
      fullScreen
      open={true}
      onClose={props.closeDialog}
      disableRestoreFocus={true}
    >
      <Game />
    </Dialog>
  </>
})