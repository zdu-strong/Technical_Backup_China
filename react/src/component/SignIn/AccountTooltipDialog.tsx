import { Dialog, DialogContent, DialogContentText, DialogTitle, Divider, Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import CloseIcon from '@mui/icons-material/Close';

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
        {"账号提示"}
      </div>
      <Fab color="primary" size="small" aria-label="add" onClick={state.closeDialog} >
        <CloseIcon />
      </Fab>
    </DialogTitle>
    <Divider />
    <DialogContent>
      <DialogContentText id="alert-dialog-description">
        {"使用账号ID, 邮箱, 手机号登陆"}
      </DialogContentText>
    </DialogContent>
  </Dialog>
})