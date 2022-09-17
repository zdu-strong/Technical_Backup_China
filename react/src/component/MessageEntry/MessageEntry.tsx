import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { Button, TextField } from "@mui/material";
import SendIcon from '@mui/icons-material/Send';
import { MessageService } from "@/common/MessageService";
import { useNavigate, useSearchParams } from "react-router-dom";
import { FormattedMessage, useIntl } from "react-intl";
import MessageChat from "@/component/Message/MessageChat";
import { GlobalUserInfo } from "@/common/axios-config/AxiosConfig";
import { useMount } from "react-use";
import api from "@/api";

export default observer(() => {

  const state = useMobxState({
    readyForStart: false,
  }, {
    ...((() => {
      const [searchParams, nextSearchParams] = useSearchParams();
      return {
        nextSearchParams,
        email: searchParams.get("email") || ""
      };
    })()),
    intl: useIntl(),
    navigate: useNavigate(),
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
    })
  });

  useMount(async () => {
    if (await api.Authorization.isSignIn()) {
      state.readyForStart = true;
    } else {
      state.navigate('/sign_in');
    }
  })

  function start() {
    const regex = new RegExp("^[A-Za-z0-9]+([_\\.][A-Za-z0-9]+)*@([A-Za-z0-9\\-]+\\.)+[A-Za-z]{2,6}$");
    if (!regex.test(state.email!)) {
      return MessageService.error(state.intl.formatMessage({
        id: "EmailAddressIsIncorrect",
        defaultMessage: "Email address is incorrect"
      }));
    }
  }

  return <div className={state.css.container}>
    {!state.readyForStart && <div className="flex flex-col justify-center" style={{ paddingLeft: "1em", paddingRight: "1em" }}>
      <TextField
        label={state.intl.formatMessage({
          id: "PleaseEnterYourEmail",
          defaultMessage: "Please enter your email"
        })}
        variant="outlined"
        onChange={(e) => {
          state.nextSearchParams({
            email: e.target.value
          }, { replace: true })
        }}
        style={{ width: "230px" }}
        value={state.email}
        onKeyDown={(e) => {
          if (!e.shiftKey && e.key === "Enter") {
            start()
          }
        }}
        autoComplete="off"
      />
      <Button
        variant="contained"
        color="secondary"
        size="large"
        style={{ marginTop: "1em", textTransform: "none", whiteSpace: "nowrap" }}
        startIcon={<SendIcon />}
        onClick={start}
      >
        <FormattedMessage id="StartUsing" defaultMessage="Start using" />
      </Button>
    </div>}
    {state.readyForStart && <MessageChat userId={GlobalUserInfo.id!} username={GlobalUserInfo.username!} />}
  </div>;

})