import { observer, useMobxState } from "mobx-react-use-autorun";
import { Alert, CircularProgress } from "@mui/material";
import GitInfo from 'react-git-info/macro'
import { useMount } from "react-use";
import { format, parseJSON } from "date-fns";
import api from "@/api";
import { GitPropertiesModel } from "@/model/GitPropertiesModel";

export default observer(() => {

  const state = useMobxState({
    clientGitInfo: GitInfo(),
    serverGitInfo: null as any as GitPropertiesModel,
    ready: false,
    error: null as any,
  })

  async function loadServerGitInfo() {
    const { data } = await api.Git.getServerGitInfo();
    state.serverGitInfo = data;
  }

  useMount(async () => {
    try {
      await loadServerGitInfo();
      state.ready = true;
    } catch {
      state.error = true;
    }
  })

  return <div className="w-full h-full flex justify-center flex-col items-center">
    {!state.error && !state.ready && <CircularProgress style={{ width: "40px", height: "40px" }} />}
    {state.error && <Alert severity="error">服务器访问出错哦</Alert>}
    {state.ready && <>
      <div>
        {`前端CommitId: ${state.clientGitInfo.commit.hash}`}
      </div>
      <div>
        {`后端Commit Id: ${state.serverGitInfo.commitId}`}
      </div>
      <div>
        {`前端更新时间: ${format(parseJSON(state.clientGitInfo.commit.date), "yyyy-MM-dd HH:mm")}`}
      </div>
      <div>
        {`后端更新时间: ${format(state.serverGitInfo.commitDate, "yyyy-MM-dd HH:mm")}`}
      </div>
    </>}
  </div>
})