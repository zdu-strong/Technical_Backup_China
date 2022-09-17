import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { useNavigate } from "react-router-dom";
import MessageChat from "@/component/Message/MessageChat";
import { GlobalUserInfo } from "@/common/axios-config/AxiosConfig";
import { useMount } from "mobx-react-use-autorun";
import api from "@/api";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { MessageService } from "@/common/MessageService";

export default observer(() => {

  const state = useMobxState({
    readyForStart: false,
    css: stylesheet({
      container: {
        width: "100%",
        height: "100%",
        flex: "1 1 auto",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
      },
    }),
  }, {
    navigate: useNavigate(),
  });

  useMount(async () => {
    try {
      if (await api.Authorization.isSignIn()) {
        state.readyForStart = true;
      } else {
        state.navigate('/sign_in');
      }
    } catch (error) {
      MessageService.error(error);
    }
  })

  return <div className={state.css.container}>
    <LoadingOrErrorComponent ready={state.readyForStart} error={null} >
      <MessageChat userId={GlobalUserInfo.id!} username={GlobalUserInfo.username!} />
    </LoadingOrErrorComponent>
  </div>;

})