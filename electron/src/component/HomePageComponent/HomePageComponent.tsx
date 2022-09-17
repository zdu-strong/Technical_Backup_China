import logo from './image/logo.svg';
import { FormattedMessage } from "react-intl";
import BootLoadingComponent from "./BootLoadingComponent";
import { Button, Link as LinkAlias } from "@mui/material";
import { keyframes, stylesheet } from 'typestyle';
import { observer, useMobxState } from 'mobx-react-use-autorun';
import { useCpuUsage } from './js/useCpuUsage';
import { useReadyForApplication } from './js/useReadyForApplication';
import CircularProgress from '@mui/material/CircularProgress';
import GameDialog from '@/component/Game/GameDialog';
import remote from '@/remote';

export default observer(() => {

  const state = useMobxState({
    gameDialog: {
      open: false,
    },
    css: stylesheet({
      container: {
        textAlign: "center",
      },
      header: {
        backgroundColor: "#282c34",
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        fontSize: "calc(10px + 2vmin)",
        color: "white",
      },
      img: {
        height: "40vmin",
        pointerEvents: "none",
        animationName: keyframes({
          "from": {
            transform: "rotate(0deg)"
          },
          "to": {
            transform: "rotate(360deg)",
          }
        }),
        animationDuration: "20s",
        animationIterationCount: "infinite",
        animationTimingFunction: "linear",
      },
      batteryContainer: {
        color: "#61dafb",
        display: "flex",
        flexDirection: "column",
      }
    }),
  }, {
    cpuUsage: useCpuUsage(),
    ready: useReadyForApplication(),
  })

  return (<>
    {!state.ready && BootLoadingComponent}
    {state.ready && <div
      className={state.css.container}
    >
      <header
        className={state.css.header}
      >
        <img
          src={logo}
          className={state.css.img} alt="logo" />
        <div className="flex">
          <FormattedMessage id="EditSrcAppTsxAndSaveToReload" defaultMessage="Edit src/App.tsx and save to reload" />
          {"."}
        </div>
        <div
          className={state.css.batteryContainer}
        >
          {
            state.cpuUsage !== null ? (<div className="flex flex-col">
              <div className="text-center">
                <FormattedMessage id="CurrentCpuUsage" defaultMessage="当前cpu使用率" />
                {":" + Math.round(state.cpuUsage!) + "%"}
              </div>
            </div>) : (
              <div className="flex flex-row justify-center">
                <CircularProgress />
              </div>
            )
          }
          <div>
            <LinkAlias underline="hover" component="div" onClick={async () => {
              state.gameDialog.open = true;
              remote.getCurrentWindow().setMenuBarVisibility(false)
              remote.getCurrentWindow().setFullScreen(true)
            }} >
              <Button variant="text" color="primary" style={{ textTransform: "none", marginTop: "1em", fontSize: "large", paddingTop: "0", paddingBottom: "0" }}>
                <FormattedMessage id="EnterTheGameIfYouWantToExitJustPressTheESCKey" defaultMessage="Enter the game, if you want to exit, just press the ESC key" />
              </Button>
            </LinkAlias>
          </div>
        </div>
      </header>
    </div>}
    {state.gameDialog.open && <GameDialog closeDialog={() => {
      remote.getCurrentWindow().setFullScreen(false)
      remote.getCurrentWindow().setMenuBarVisibility(true)
      state.gameDialog.open = false;
    }} />}
  </>);
})