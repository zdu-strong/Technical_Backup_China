import { Button, Dialog, DialogActions, DialogTitle, Fab } from "@mui/material";
import { observer } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import CloseIcon from '@mui/icons-material/Close';
import { App } from '@capacitor/app'

export default observer((props: { closeDialog: () => void, exit: () => void }) => {
  return <Dialog
    open={true}
    onClose={props.closeDialog}
    aria-labelledby="alert-dialog-title"
    aria-describedby="alert-dialog-description"
  >
    <DialogTitle
      id="alert-dialog-title"
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <div style={{ fontWeight: "bold" }}>
        <FormattedMessage id="Quit" defaultMessage="Quit?" />
      </div>
      <Fab size="small" color="default" onClick={props.closeDialog}>
        <CloseIcon />
      </Fab>
    </DialogTitle>
    <DialogActions>
      <Button
        onClick={() => {
          props.closeDialog();
          props.exit();
        }}
        variant="contained"
        style={{ textTransform: "none", marginLeft: "1em" }}
        color="primary"
      >
        <FormattedMessage id="ExitTheGame" defaultMessage="Exit Game" />
      </Button>
      <Button
        variant="contained"
        style={{ textTransform: "none", marginRight: "1em" }}
        onClick={() => {
          App.exitApp()
        }}
        color="secondary"
      >
        <FormattedMessage id="ExitTheApp" defaultMessage="Exit App" />
      </Button>
    </DialogActions>
  </Dialog>
})