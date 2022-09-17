import { Button, CircularProgress, TextField } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage, useIntl } from "react-intl";
import { stylesheet } from "typestyle";
import HelpIcon from '@mui/icons-material/Help';
import LoginIcon from '@mui/icons-material/Login';
import AccountTooltipDialog from "./AccountTooltipDialog";
import PasswordTooltipDialog from "./PasswordTooltipDialog";
import api from "@/api";
import { MessageService } from "@/common/MessageService";
import { timer } from "rxjs";
import { Link, useNavigate } from "react-router-dom";

export default observer(() => {

  const state = useMobxState({
    username: 'zdu.strong@gmail.com',
    password: 'zdu.strong@gmail.com',
    submitted: false,
    usernameTooltipDialog: {
      open: false,
    },
    passwordTooltipDialog: {
      open: false,
    },
    loading: {
      signIn: false,
    },
    showPasswordInput: false,
    error: {
      username() {
        if (state.username) {
          if (state.username.replaceAll(new RegExp('^\\s+', 'g'), '').length !== state.username.length) {
            return '账号的开头不能有空格'
          }
          if (state.username.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.username.length) {
            return '账号的结尾不能有空格'
          }
        }
        if (!state.submitted) {
          return false;
        }
        if (!state.username) {
          return '请填写账号';
        }
        return false;
      },
      password() {
        if (state.password) {
          if (state.password.replaceAll(new RegExp('^\\s+', 'g'), '').length !== state.password.length) {
            return '密码的开头不能有空格'
          }
          if (state.password.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.password.length) {
            return '密码的结尾不能有空格'
          }
        }
        if (!state.submitted) {
          return false;
        }
        if (!state.password) {
          return '请填写密码'
        }
        return false;
      },
      hasError() {
        return Object.keys(state.error).filter(s => s !== "hasError").some(s => (state.error as any)[s]());
      }
    },
  }, {
    css: stylesheet({
      container: {
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        paddingTop: "1em",
        paddingLeft: "5em",
        paddingRight: "5em",
      }
    }),
    intl: useIntl(),
    navigate: useNavigate(),
  })

  async function signIn() {
    if (state.loading.signIn) {
      return;
    }
    state.submitted = true;
    if (state.error.hasError()) {
      return;
    }
    try {
      state.loading.signIn = true;
      state.showPasswordInput = false;
      await timer(500).toPromise();
      await api.Authorization.signIn(state.username, state.password);
      state.navigate("/chat")
    } catch (e) {
      MessageService.error(e);
    } finally {
      state.loading.signIn = false;
    }
  }

  return <div className={state.css.container}>
    <div>
      <FormattedMessage id="SignIn" defaultMessage="SignIn" />
    </div>
    <div style={{ marginTop: "1em" }} className="flex flex-col w-full">
      <TextField
        label={state.intl.formatMessage({
          id: "AccountID",
          defaultMessage: "Account ID"
        })}
        variant="outlined"
        onChange={(e) => {
          state.username = e.target.value;
        }}
        value={state.username}
        autoComplete="off"
        error={!!state.error.username()}
        helperText={state.error.username()}
        InputProps={{
          endAdornment: <HelpIcon color="primary" style={{ cursor: "pointer" }} onClick={() => state.usernameTooltipDialog.open = true} />
        }}
      />
    </div>
    <div style={{ marginTop: "1em" }} className="flex flex-col w-full">
      {state.showPasswordInput && <TextField
        label={state.intl.formatMessage({
          id: "Password",
          defaultMessage: "Password"
        })}
        className="flex flex-auto"
        variant="outlined"
        onChange={(e) => {
          state.password = e.target.value;
        }}
        inputProps={{
          style: {
            resize: "vertical",
          }
        }}
        value={state.password}
        autoComplete="off"
        multiline={true}
        rows={6}
        error={!!state.error.password()}
        helperText={state.error.password()}
        InputProps={{
          endAdornment: <HelpIcon color="primary" style={{ cursor: "pointer" }} onClick={() => state.passwordTooltipDialog.open = true} />
        }}
      />}
      {!state.showPasswordInput && <Button variant="outlined" className="w-full" onClick={() => {
        state.showPasswordInput = true
      }}>
        {"密码已填写, 点击编辑"}
      </Button>}
    </div>
    <div style={{ marginTop: "1em" }}>
      <Button variant="contained" startIcon={state.loading.signIn ? <CircularProgress color="inherit" size="16px" /> : <LoginIcon />} onClick={signIn}>
        {"登陆"}
      </Button>
    </div>
    <div className="w-full" style={{ marginTop: "2em" }}>
      <Link to="/sign_up">
        {"注册"}
      </Link>
    </div>
    {state.usernameTooltipDialog.open && <AccountTooltipDialog
      closeDialog={() => state.usernameTooltipDialog.open = false}
    />}
    {state.passwordTooltipDialog.open && <PasswordTooltipDialog
      closeDialog={() => state.passwordTooltipDialog.open = false}
    />}
  </div>;
})