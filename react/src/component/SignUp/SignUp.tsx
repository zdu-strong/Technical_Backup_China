import { Button, CircularProgress, Divider, Fab, IconButton, TextField } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage, useIntl } from "react-intl";
import { stylesheet } from "typestyle";
import { v1 } from "uuid";
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import SaveIcon from '@mui/icons-material/Save';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import { useMount } from "react-use";
import api from "@/api";
import NodeRSA from 'node-rsa'
import { MessageService } from "@/common/MessageService";
import { timer } from "rxjs";
import { UserEmailModel } from "@/model/UserEmailModel";
import SendIcon from '@mui/icons-material/Send';
import { Link, useNavigate } from "react-router-dom";

export default observer(() => {

  const state = useMobxState({
    nickname: '',
    userId: v1(),
    password: '',
    emailList: [] as UserEmailModel[],
    steps: [
      '设置呢称',
      '设置密码',
      '绑定邮箱或手机号',
      '完成'
    ],
    activeStep: 0,
    publicKeyOfRSA: '',
    submitted: false,
    loading: {
      signUp: false,
      sendVerificationCode: {} as Record<string, boolean>,
    },
    ready: false,
    error: {
      nickname() {
        if (state.nickname) {
          if (state.nickname.replaceAll(new RegExp('^\\s+', 'g'), '').length !== state.nickname.length) {
            return '呢称的开头不能有空格'
          }
          if (state.nickname.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.nickname.length) {
            return '呢称的结尾不能有空格'
          }
        }
        if (!state.submitted) {
          return false;
        }
        if (!state.nickname) {
          return '请填写昵称';
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
      email(emailInfo: UserEmailModel) {
        if (!state.submitted) {
          return false;
        }
        if (!emailInfo.email) {
          return '请填写邮箱'
        }
        if (!new RegExp('^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$').test(emailInfo.email)) {
          return '邮箱格式不正确'
        }
        return false;
      },
      verificationCode(emailInfo: UserEmailModel) {
        if (!state.submitted) {
          return false;
        }
        if (!emailInfo.verificationCode) {
          return '请填写验证码'
        }
        return false;
      }
    }
  }, {
    css: stylesheet({
      container: {
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        flex: "1 1 auto",
        width: "100%",
        paddingLeft: "5em",
        paddingRight: "5em",
      }
    }),
    intl: useIntl(),
    navigate: useNavigate(),
  })

  useMount(async () => {
    try {
      const { data: user } = await api.Authorization.getNewAccountOfSignUp();
      state.userId = user.id!;
      state.publicKeyOfRSA = user.publicKeyOfRSA!;
      state.ready = true;
    } catch (e) {
      MessageService.error(e);
    }
  })

  async function signUp() {
    if (state.loading.signUp) {
      return;
    }

    try {
      state.loading.signUp = true
      await timer(500).toPromise();
      var userEmailList = state.emailList.map(s => ({
        email: s.email,
        verificationCode: s.verificationCode,
      }));
      await api.Authorization.signUp(state.userId, state.password, state.nickname, userEmailList, state.publicKeyOfRSA);
      state.navigate("/chat")
    } catch (e) {
      MessageService.error(e);
    } finally {
      state.loading.signUp = false
    }
  }
  function nextStep() {
    state.submitted = true;
    if (state.activeStep === 0 && state.error.nickname()) {
      return;
    }
    if (state.activeStep === 1 && state.error.password()) {
      return;
    }
    if (state.activeStep === 2 && state.emailList.some(s => state.error.email(s))) {
      return;
    }
    if (state.activeStep === 2 && state.emailList.some(s => state.error.verificationCode(s))) {
      return;
    }
    state.submitted = false

    if (state.activeStep < state.steps.length - 1) {
      state.activeStep++;
    }
  }

  return <div className={state.css.container}>
    {!state.ready && <div className="flex flex-col flex-auto justify-center">
      <CircularProgress color="secondary" />
    </div>}
    {state.ready && <div className="flex flex-col flex-auto w-full">
      <div className="flex flex-col flex-auto w-full">
        <div className="flex justify-center" style={{ marginTop: "1em" }}>
          <FormattedMessage id="SignUp" defaultMessage="SignUp" />
        </div>
        <Divider style={{ marginTop: "1em" }} />
        <div className="flex flex-row" style={{ marginTop: "1em" }}>
          <div style={{ marginRight: "1em" }}>
            <FormattedMessage id="AccountID" defaultMessage="Account ID" />
            {":"}
          </div>
          <div>
            {state.userId}
          </div>
        </div>
        {state.activeStep === 0 && <div>
          {"你可以使用账号ID, 邮箱, 手机号作为账号进行登陆"}
        </div>}
        <Divider style={{ marginTop: "1em" }} />
        {state.activeStep === 0 && <div className="flex flex-col" style={{ marginTop: "1em" }}>
          <div>
            {"昵称, 你可以随时修改您的昵称"}
          </div>
          <TextField
            label={state.intl.formatMessage({
              id: "Nickname",
              defaultMessage: "nickname"
            })}
            variant="outlined"
            onChange={(e) => {
              state.nickname = e.target.value;
            }}
            value={state.nickname}
            autoComplete="off"
            error={!!state.error.nickname()}
            helperText={state.error.nickname()}
            style={{ marginTop: "1em" }}
            onKeyDown={(e) => {
              if (!e.shiftKey && e.key === "Enter") {
                nextStep();
              }
            }}
          />
        </div>}
        {state.activeStep !== 0 && (<div className="flex flex-row" style={{ marginTop: "1em" }}>
          <div style={{ marginRight: "1em" }}>
            <FormattedMessage id="Nickname" defaultMessage="nickname" />
            {":"}
          </div>
          <div>
            {state.nickname}
          </div>
        </div>)}
        {state.activeStep > 0 && <Divider style={{ marginTop: "1em", marginBottom: "1em" }} />}
        {state.activeStep > 1 && <div>
          {"密码设置完成"}
        </div>}
        {state.activeStep === 1 && <div className="flex flex-col">
          <div className="flex" style={{ marginBottom: "0em" }} >
            {"就像藏宝图一样, 让我们把密码藏在这个世界上. 比如, 从莎士比亚著作中选择一段作为密码."}
          </div>
          <div className="flex" style={{ marginBottom: "0em" }} >
            {"密码支持UTF-8的所有字符, 你可以使用你喜欢的任何语言内容作为密码."}
          </div>
          <div className="flex" style={{ marginBottom: "0em" }} >
            {"登陆后, 你可以随时更改你的密码."}
          </div>
          <div className="flex" style={{ marginBottom: "0em" }}>
            {"我们不提供忘记密码功能. 所以, 请记住你的密码."}
          </div>
          <div className="flex" style={{ marginBottom: "1em" }}>
            {"我们无法知道你的聊天内容和文件内容, 能知道的只有持有密码的你."}
          </div>
          <TextField
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
          />
        </div>}
        {state.activeStep > 1 && <Divider style={{ marginTop: "1em", marginBottom: "1em" }} />}
        {state.activeStep > 2 && <div>
          {state.emailList.length > 0 && <div className="flex flex-col">
            {state.emailList.map(s => <div key={s.id}>
              {"邮箱: "}
              {s.email}
            </div>)}
          </div>}
        </div>}
        {state.activeStep === 2 && <div className="flex flex-col">
          <div>
            {"绑定邮箱或手机号"}
          </div>
          <div>
            {"你可以使用邮箱或手机号作为账号进行登陆, 同时也能在登陆页面进行解绑."}
          </div>
          <div>
            {"如果你不幸忘记密码, 请创建新账号, 然后在用户设置界面使用邮箱或手机号找回曾经绑定过的账号的好友关系."}
          </div>
          {state.emailList.map((s, index) => <div className="flex flex-row items-center" key={s.id} style={{ marginTop: '1em' }}>
            <TextField
              label={state.intl.formatMessage({
                id: "Email",
                defaultMessage: "Email"
              })}
              variant="outlined"
              onChange={(e) => {
                s.email = e.target.value;
              }}
              value={s.email}
              autoComplete="off"
              className="w-full"
              error={!!state.error.email(s)}
              helperText={state.error.email(s)}
            />
            <div>
              <Button
                style={{ marginLeft: "1em" }}
                variant="contained"
                onClick={async () => {
                  try {
                    state.submitted = true;
                    if (state.error.email(s)) {
                      return;
                    }
                    state.submitted = false;

                    state.loading.sendVerificationCode[s.id!] = true;
                    const rsa = new NodeRSA(state.publicKeyOfRSA, "pkcs8-public", { encryptionScheme: "pkcs1" });
                    await api.Authorization.sendVerificationCode(state.userId, s.email!, rsa.encrypt(state.userId, 'base64'));
                  } catch (e) {
                    MessageService.error(e);
                  } finally {
                    state.loading.sendVerificationCode[s.id!] = false;
                  }
                }}
                startIcon={state.loading.sendVerificationCode[s.id!] ? <CircularProgress color="inherit" size="16px" /> : <SendIcon />}
              >
                {"Send"}
              </Button>
            </div>
            <TextField
              label={"验证码"}
              variant="outlined"
              onChange={(e) => {
                if (e.target.value === '') {
                  s.verificationCode = e.target.value;
                } else if (e.target.value && new RegExp("^[0-9]+$").test(e.target.value)) {
                  s.verificationCode = e.target.value;
                }
              }}
              value={s.verificationCode}
              autoComplete="off"
              className="w-full"
              error={!!state.error.verificationCode(s)}
              helperText={state.error.verificationCode(s)}
              style={{ marginLeft: "1em" }}
            />
            <IconButton
              aria-label="delete"
              onClick={() => {
                const index = state.emailList.findIndex(m => m.id === s.id);
                if (index >= 0) {
                  state.emailList.splice(index, 1)
                }
              }}
              style={{ marginLeft: "0.2em" }}
              size="large"
            >
              <DeleteIcon />
            </IconButton>
          </div>)}
          <div style={{ marginTop: "0.9em" }}>
            <Fab
              color="primary"
              aria-label="add"
              size="small"
              onClick={() => {
                state.emailList.push({
                  id: v1(),
                  email: '',
                  verificationCode: '',
                })
              }}
            >
              <AddIcon />
            </Fab>
          </div>
        </div>}
      </div>

      <Divider style={{ marginTop: "1em", marginBottom: "1em" }} />
      <div style={{ marginTop: "1em" }} className="flex flex-col">
        <Box sx={{ width: '100%' }}>
          <Stepper activeStep={state.activeStep} alternativeLabel>
            {state.steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>
        </Box>
        <div className="flex justify-between" style={{ marginTop: "1em", marginBottom: "1em" }}>
          {state.activeStep === 0 && <div></div>}
          {state.activeStep > 0 && <Button
            variant="contained"
            startIcon={<SaveIcon />}
            onClick={() => {
              if (state.activeStep > 0) {
                state.activeStep--;
              }
            }}
          >
            {"上一步"}
          </Button>}
          <Link to="/sign_in">
            {"登陆"}
          </Link>
          {state.activeStep < state.steps.length - 1 && <Button
            variant="contained"
            startIcon={<SaveIcon />}
            onClick={nextStep}
          >
            {"下一步"}
          </Button>}
          {state.activeStep >= state.steps.length - 1 && <Button
            variant="contained"
            startIcon={state.loading.signUp ? <CircularProgress color="inherit" size="16px" /> : <SaveIcon />}
            onClick={signUp}
          >
            {"注册"}
          </Button>}
        </div>
      </div>
      <div>
      </div>
    </div>}
  </div>;
})