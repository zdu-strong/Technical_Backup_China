import { Alert, Skeleton } from "@mui/material"
import React from "react";
import { MESSAGE_TYPE_ENUM, getMessage } from ".";
import { observer, useMobxEffect, useMobxState } from 'mobx-react-use-autorun';

export default observer((props: { ready: boolean, error: any, children?: React.ReactNode }) => {

  const state = useMobxState({
    errorMessage: "",
  }, {
    ...props
  });

  useMobxEffect(() => {
    state.errorMessage = getMessage(MESSAGE_TYPE_ENUM.error, state.error).message;
  }, [state.error])

  return <>
    {state.error && <Alert severity="error" style={{ margin: "1em" }}>
      {state.errorMessage}
    </Alert>}
    {!state.error && !state.ready && <div className="flex flex-auto flex-col" style={{ minHeight: "10em", minWidth: "100%", padding: "1em" }}>
      <Skeleton variant="text" />
      <Skeleton variant="circular" width={40} height={40} />
      <Skeleton variant="rectangular" className="flex flex-auto" />
    </div>}
    {!state.error && state.ready && state.children}
  </>
})