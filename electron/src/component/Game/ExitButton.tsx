import { Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import ExitDialog from "@/component/Game/ExitDialog";
import { useMount } from "mobx-react-use-autorun";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGear } from '@fortawesome/free-solid-svg-icons'

const css = stylesheet({
  container: {
    width: "100%",
    height: "0px",
    position: "relative",
    display: "flex",
    flexDirection: "row",
  },
  exitButton: {
    position: "absolute",
    top: "10px"
  }
})

export default observer((props: {
  exit: () => void,
  canvasRef: React.MutableRefObject<HTMLCanvasElement | undefined>
}) => {

  const state = useMobxState({
    exitDialog: {
      open: false,
    },
    ready: false,
    leftOrRight: 10,

  }, {
    ...props,
  })

  useMount(() => {
    state.ready = true;
  })

  return <>
    <div className={css.container}>
      {state.ready && <Fab
        size="small"
        color="primary"
        aria-label="add"
        style={{ right: `${state.leftOrRight}px`, position: "absolute" }}
        className={css.exitButton}
        onClick={() => {
          state.exitDialog.open = true
        }}
      >
        <FontAwesomeIcon icon={faGear} size="xl" />
      </Fab>}
    </div>
    {state.exitDialog.open && <ExitDialog
      canvasRef={state.canvasRef}
      exit={state.exit}
      closeDialog={() => {
        state.exitDialog.open = false
      }}
    />}
  </>
})