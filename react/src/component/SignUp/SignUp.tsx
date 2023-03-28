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
import { useMount } from "mobx-react-use-autorun";
import api from "@/api";
import { MessageService } from "@/common/MessageService";
import { UserEmailModel } from "@/model/UserEmailModel";
import SendIcon from '@mui/icons-material/Send';
import { Link, useNavigate } from "react-router-dom";
import { encryptByPublicKeyOfRSA } from "@/common/RSAUtils";

export default observer(() => {

  const state = useMobxState({
    nickname: '',
    userId: v1(),
    password: '',
    emailList: [] as UserEmailModel[],
    steps: [] as string[],
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
            return state.intl.formatMessage({ id: "ThereShouldBeNoSpacesAtTheBeginningOfTheNickname", defaultMessage: "There should be no spaces at the beginning of the nickname" })
          }
          if (state.nickname.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.nickname.length) {
            return state.intl.formatMessage({ id: "TheNicknameCannotHaveASpaceAtTheEnd", defaultMessage: "The nickname cannot have a space at the end" })
          }
        }
        if (!state.submitted) {
          return false;
        }
        if (!state.nickname) {
          return state.intl.formatMessage({ id: "PleaseFillInNickname", defaultMessage: "Please fill in nickname" })
        }
        return false;
      },
      password() {
        if (state.password) {
          if (state.password.replaceAll(new RegExp('^\\s+', 'g'), '').length !== state.password.length) {
            return state.intl.formatMessage({ id: "PasswordMustNotHaveSpacesAtTheBeginning", defaultMessage: "Password must not have spaces at the beginning" })
          }
          if (state.password.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.password.length) {
            return state.intl.formatMessage({ id: "PasswordCannotHaveASpaceAtTheEnd", defaultMessage: "Password cannot have a space at the end" })
          }
        }
        if (!state.submitted) {
          return false;
        }
        if (!state.password) {
          return state.intl.formatMessage({ id: "PleaseFillInThePassword", defaultMessage: "Please fill in the password" })
        }
        return false;
      },
      email(emailInfo: UserEmailModel) {
        if (!state.submitted) {
          return false;
        }
        if (!emailInfo.email) {
          return state.intl.formatMessage({ id: "PleaseEnterYourEmail", defaultMessage: "Please enter your email" })
        }
        if (!new RegExp('^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$').test(emailInfo.email)) {
          return state.intl.formatMessage({ id: "EMailFormatIsIncorrect", defaultMessage: "E-mail format is incorrect" })
        }
        return false;
      },
      verificationCode(emailInfo: UserEmailModel) {
        if (!state.submitted) {
          return false;
        }
        if (!emailInfo.verificationCode) {
          return state.intl.formatMessage({ id: "PleaseFillInTheVerificationCode", defaultMessage: "Please fill in the verification code" })
        }
        return false;
      }
    },
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
  }, {
    intl: useIntl(),
    navigate: useNavigate(),
  })

  useMount(async () => {
    try {
      await initSteps();
      await getNewAccountOfSignUp();
      state.ready = true;
    } catch (e) {
      MessageService.error(e);
    }
  })

  async function initSteps() {
    state.steps.splice(0, state.steps.length);
    state.steps.push(state.intl.formatMessage({ id: "SetNickname", defaultMessage: "Set nickname" }));
    state.steps.push(state.intl.formatMessage({ id: "SetPassword", defaultMessage: "Set password" }));
    state.steps.push(state.intl.formatMessage({ id: "BindEmailOrMobilePhoneNumber", defaultMessage: "Bind email or mobile phone number" }));
    state.steps.push(state.intl.formatMessage({ id: "Complete", defaultMessage: "Complete" }));
  }

  async function getNewAccountOfSignUp() {
    const { data: user } = await api.Authorization.getNewAccountOfSignUp();
    state.userId = user.id;
    state.publicKeyOfRSA = user.publicKeyOfRSA;
  }

  async function signUp() {
    if (state.loading.signUp) {
      return;
    }

    try {
      state.loading.signUp = true
      const userEmailList = state.emailList.map(s => ({
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
          <FormattedMessage id="YouCanSignInWithYourAccountIDEmailOrMobilePhoneNumberAsAnAccount" defaultMessage="You can log in with your account ID, mailbox, mobile phone number as an account" />
        </div>}
        <Divider style={{ marginTop: "1em" }} />
        {state.activeStep === 0 && <div className="flex flex-col" style={{ marginTop: "1em" }}>
          <div>
            <FormattedMessage id="NicknameYouCanModifyYourNicknameAtAnyTime" defaultMessage="Nickname, you can modify your nickname at any time" />
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
          <FormattedMessage id="PasswordSettingIsComplete" defaultMessage="Password setting is complete" />
        </div>}
        {state.activeStep === 1 && <div className="flex flex-col">
          <div className="flex" style={{ marginBottom: "0em" }} >
            <FormattedMessage id="JustLikeTheTreasureMapLetUsHideThePasswordInThisWorld" defaultMessage="Just like the treasure map, let's hide the password in this world. For example, select a paragraph as a password from Shakespeare's works." />
          </div>
          <div className="flex" style={{ marginBottom: "0em" }} >
            <FormattedMessage id="ThePasswordSupportsAllTheCharactersOfUTF8" defaultMessage="The password supports all the characters of UTF-8. You can use any of the language content you like as the password." />
          </div>
          <div className="flex" style={{ marginBottom: "0em" }}>
            <FormattedMessage id="WeDoNotProvideResetPasswordSoRememberYourPassword" defaultMessage="We do not provide reset password functions. So, remember your password." />
          </div>
          <div className="flex" style={{ marginBottom: "1em" }}>
            <FormattedMessage id="OnlyYouWhocanKnowThePassword" defaultMessage="We can't know your chat content and file content. Only you who can know the password." />
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
              <FormattedMessage id="Email" defaultMessage="Email" />
              {": "}
              {s.email}
            </div>)}
          </div>}
        </div>}
        {state.activeStep === 2 && <div className="flex flex-col">
          <div>
            <FormattedMessage id="BindedEmailOrMobilePhoneNumber" defaultMessage="Binded email or mobile phone number" />
          </div>
          <div>
            <FormattedMessage id="YouCanUseTheEmailOrMobilePhoneNumberAsAnAccountToSignIn" defaultMessage="You can use the mailbox or mobile phone number as an account to log in, and you can also unbind on the login page." />
          </div>
          <div>
            <FormattedMessage id="IfYouUnfortunatelyForgetYourPasswordPleaseCreateANewAccount" defaultMessage="If you unfortunately forget your password, please create a new account, and then use an email or mobile phone number to retrieve the friend relationship of the account that you have bound to use the mailbox or mobile phone number in the user setting interface." />
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
                style={{
                  marginLeft: "1em",
                  textTransform: "none"
                }}
                variant="contained"
                onClick={async () => {
                  try {
                    state.submitted = true;
                    if (state.error.email(s)) {
                      return;
                    }
                    state.submitted = false;

                    state.loading.sendVerificationCode[s.id!] = true;
                    await api.Authorization.sendVerificationCode(state.userId, s.email!, await encryptByPublicKeyOfRSA(state.publicKeyOfRSA, state.userId));
                  } catch (e) {
                    MessageService.error(e);
                  } finally {
                    state.loading.sendVerificationCode[s.id!] = false;
                  }
                }}
                startIcon={state.loading.sendVerificationCode[s.id!] ? <CircularProgress color="inherit" size="16px" /> : <SendIcon />}
              >
                <FormattedMessage id="Send" defaultMessage="Send" />
              </Button>
            </div>
            <TextField
              label={<FormattedMessage id="VerificationCode" defaultMessage="Verification code" />}
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
            style={{
              textTransform: "none"
            }}
            variant="contained"
            startIcon={<SaveIcon />}
            onClick={() => {
              if (state.activeStep > 0) {
                state.activeStep--;
              }
            }}
          >
            <FormattedMessage id="Previous" defaultMessage="Previous" />
          </Button>}
          <Link to="/sign_in">
            <FormattedMessage id="SignIn" defaultMessage="SignIn" />
          </Link>
          {state.activeStep < state.steps.length - 1 && <Button
            variant="contained"
            startIcon={<SaveIcon />}
            onClick={nextStep}
            style={{
              textTransform: "none"
            }}
          >
            <FormattedMessage id="Next" defaultMessage="Next" />
          </Button>}
          {state.activeStep >= state.steps.length - 1 && <Button
            variant="contained"
            startIcon={state.loading.signUp ? <CircularProgress color="inherit" size="16px" /> : <SaveIcon />}
            onClick={signUp}
            style={{
              textTransform: "none"
            }}
          >
            <FormattedMessage id="SignUp" defaultMessage="SignUp" />
          </Button>}
        </div>
      </div>
      <div>
      </div>
    </div>}
  </div>;
})