import { Alert, Skeleton } from "@mui/material";
import { observer } from "mobx-react-use-autorun";

export default observer((props: { loading: boolean, error: any }) => {

  return <>
    {props.error && <div className="flex flex-auto w-full h-full">
      <Alert severity="error">{String(props.error)}</Alert>
    </div>}
    {!props.error && props.loading && <div className="flex flex-auto w-full h-full flex-col">
      <Skeleton variant="rectangular" className="w-full" height={60} />
      <div className="flex flex-row w-full">
        <Skeleton variant="circular" width={40} height={40} />
      </div>
      <Skeleton variant="rectangular" className="flex flex-auto" height={60} />
    </div>}
  </>
})