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
        {"密码提示"}
      </div>
      <Fab color="primary" size="small" aria-label="add" onClick={state.closeDialog} >
        <CloseIcon />
      </Fab>
    </DialogTitle>
    <Divider />
    <DialogContent>
      <DialogContentText id="alert-dialog-description">
        {"就像藏宝图一样, 让我们把密码藏在这个世界上. 比如, 从莎士比亚著作中选择一段作为密码."}
      </DialogContentText>
    </DialogContent>
  </Dialog>
})