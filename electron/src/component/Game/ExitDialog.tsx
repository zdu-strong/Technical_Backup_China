import { Button, Dialog, DialogActions, DialogTitle, Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import CloseIcon from '@mui/icons-material/Close';

export default observer((props: { closeDialog: () => void, exit: () => void }) => {

  const state = useMobxState({}, {
    ...props
  })

  return <Dialog
    open={true}
    onClose={state.closeDialog}
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
      <div style={{ fontWeight: "bold", marginRight: "2em" }}>
        <FormattedMessage id="Quit" defaultMessage="Quit?" />
      </div>
      <Fab size="small" color="default" onClick={state.closeDialog}>
        <CloseIcon />
      </Fab>
    </DialogTitle>
    <DialogActions className="flex justify-end">
      <Button
        onClick={() => {
          state.closeDialog();
          state.exit();
        }}
        variant="contained"
        style={{ textTransform: "none", marginLeft: "1em" }}
        color="primary"
      >
        <FormattedMessage id="Yes" defaultMessage="Yes" />
      </Button>
    </DialogActions>
  </Dialog>
})