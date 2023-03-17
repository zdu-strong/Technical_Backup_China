import { Dialog, DialogContent, DialogContentText, DialogTitle, Divider, Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import CloseIcon from '@mui/icons-material/Close';
import { FormattedMessage } from "react-intl";

export default observer((props: {
  closeDialog: () => void,
}) => {

  const state = useMobxState({}, {
    ...props
  })

  return <Dialog
    open={true}
    onClose={state.closeDialog}
    aria-labelledby="alert-dialog-title"
    aria-describedby="alert-dialog-description"
  >
    <DialogTitle id="alert-dialog-title" className="flex flex-row justify-between items-center">
      <div>
        <FormattedMessage id="AccountTip" defaultMessage="Account tip" />
      </div>
      <Fab color="primary" size="small" aria-label="add" onClick={state.closeDialog} >
        <CloseIcon />
      </Fab>
    </DialogTitle>
    <Divider />
    <DialogContent>
      <DialogContentText id="alert-dialog-description">
        <FormattedMessage id="UseAccountIDEmailOrMobilePhoneNumberToSignIn" defaultMessage="Use Account ID, email or mobile phone number to signIn" />
      </DialogContentText>
    </DialogContent>
  </Dialog>
})