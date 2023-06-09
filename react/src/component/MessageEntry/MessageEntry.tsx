import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { useNavigate } from "react-router-dom";
import MessageChat from "@/component/Message/MessageChat";
import { GlobalUserInfo } from "@/common/axios-config/AxiosConfig";
import { useMount } from "mobx-react-use-autorun";
import api from "@/api";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { v1 } from "uuid";
import MessageMenu from "@/component/MessageEntry/MessageMenu";
import MessageUnlimitedList from "@/component/Message/MessageUnlimitedList";

export default observer(() => {

  const state = useMobxState({
    readyForStart: false,
    readyForMessageList: false,
    async setReadyForMessageList(readyForMessageList: boolean) {
      state.readyForMessageList = readyForMessageList;
    },
    error: null as any,
    css: stylesheet({
      container: {
        width: "100%",
        height: "100%",
        flex: "1 1 auto",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        paddingLeft: "1em",
        paddingRight: "1em",
      },
    }),
  }, {
    navigate: useNavigate(),
  });

  useMount(async () => {
    try {
      if (!(await api.Authorization.isSignIn())) {
        const { data: newAccount } = await api.Authorization.createNewAccountOfSignUp();
        await api.Authorization.signUp(newAccount.id, v1(), "visitor", [], newAccount.publicKeyOfRSA);
      }
      state.readyForStart = true;
    } catch (error) {
      state.error = error;
    }
  })

  return <>
    <LoadingOrErrorComponent ready={state.readyForStart && state.readyForMessageList} error={state.error} />
    {state.readyForStart && <div className={state.css.container} style={state.readyForMessageList ? {} : { position: "absolute", visibility: "hidden" }} >
      <MessageMenu userId={GlobalUserInfo.id} username={GlobalUserInfo.username} />
      <MessageUnlimitedList userId={GlobalUserInfo.id!} username={GlobalUserInfo.username!} setReadyForMessageList={state.setReadyForMessageList} />
      <MessageChat userId={GlobalUserInfo.id!} username={GlobalUserInfo.username!} />
    </div>}
  </>
})