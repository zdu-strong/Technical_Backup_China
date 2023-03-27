import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { MessageService } from "@/common/MessageService";
import api from '@/api'
import { AppBar, BottomNavigation, BottomNavigationAction, Box, Button, CircularProgress, Fab, TextField, Toolbar, Typography } from "@mui/material";
import SendIcon from '@mui/icons-material/Send'
import { FormattedMessage, useIntl } from "react-intl";
import { v1 } from 'uuid'
import { isMobilePhone } from "@/common/is-mobile-phone";
import AddIcon from '@mui/icons-material/Add';
import MessageUnlimitedList from "./MessageUnlimitedList";
import BlindsIcon from '@mui/icons-material/Blinds';
import AssignmentIcon from '@mui/icons-material/Assignment';
import MessagePagination from "./MessagePagination";
import { concatMap, from, map, toArray } from "rxjs";
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { useRef } from "react";
import MessageMoreActionDialog from "../MessageMoreAction/MessageMoreActionDialog";
import { useNavigate } from "react-router-dom";

export default observer((props: { username: string, userId: string }) => {
  const state = useMobxState({
    /* 待发送的消息 */
    messageContent: "",
    /* 是否正在发送消息 */
    loadingOfSend: false,
    chatTechnology: {
      mode: "infiniteList",
      pagination: "pagination",
      infiniteList: "infiniteList",
    },
    inputFileId: v1(),
    messageInputId: v1(),
    textareaRef: useRef<HTMLTextAreaElement>(),
    moreActionDialog: {
      open: false,
    },
    css: stylesheet({
      messsageListContainer: {
        width: "100%",
        height: "100%",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        paddingLeft: "1em",
        paddingRight: "1em",
        flex: "1 1 auto",
      },
    }),
  }, {
    ...props,
    intl: useIntl(),
    inputFileRef: useRef<any>(),
    navigate: useNavigate(),
  })

  async function sendMessage() {
    if (!state.messageContent) {
      return MessageService.error(state.intl.formatMessage({
        id: "PleaseFillInTheMessageContent",
        defaultMessage: "Please fill in the message content"
      }));
    }
    if (state.loadingOfSend) {
      return;
    }
    try {
      state.loadingOfSend = true;
      await api.UserMessage.sendMessage({
        content: state.messageContent,
      });
      state.messageContent = "";
    } catch (error) {
      MessageService.error(state.intl.formatMessage({
        id: "FailedToSend",
        defaultMessage: "Failed to send"
      }))
    } finally {
      state.loadingOfSend = false;
    }
  }

  async function sendMessageForFileList(fileList: FileList) {
    if (isMobilePhone) {
      state.moreActionDialog.open = false;
      state.textareaRef.current?.blur();
      state.textareaRef.current?.focus();
    }

    if (state.loadingOfSend) {
      return;
    }

    state.inputFileId = v1();
    if (fileList.length > 0) {
      try {
        state.loadingOfSend = true;
        const urlList = (await from(fileList).pipe(
          concatMap((file) => from(api.upload(file))),
          map(({ url }) => url),
          toArray(),
        ).toPromise())! as string[];
        for (const url of urlList) {
          await api.UserMessage.sendMessage({
            url,
          });
        }
      } catch (error) {
        MessageService.error(state.intl.formatMessage({
          id: "FailedToSend",
          defaultMessage: "Failed to send"
        }))
      } finally {
        state.loadingOfSend = false;
      }
    }
  }

  return <div className={state.css.messsageListContainer}>
    <Box style={{ marginBottom: "1em", width: "100%" }}>
      <AppBar position="static">
        <Toolbar className="flex flex-row justify-between" style={{ paddingLeft: "0px" }}>
          <BottomNavigation
            showLabels
            value={state.chatTechnology.mode}
            onChange={(event, newValue) => {
              state.chatTechnology.mode = newValue;
            }}
          >
            <BottomNavigationAction value={state.chatTechnology.infiniteList} label={<FormattedMessage id="Unlimited" defaultMessage="Unlimited" />} icon={<AssignmentIcon />} />
            <BottomNavigationAction value={state.chatTechnology.pagination} label={<FormattedMessage id="Pagination" defaultMessage="Pagination" />} icon={<BlindsIcon />} />
          </BottomNavigation>
          <div className="flex flex-row">
            <Typography variant="h6" component="div" sx={{ flexWrap: "nowrap" }}>
              <div style={{ marginLeft: "1em", ...(isMobilePhone ? { fontSize: "x-small" } : {}) }}>
                {state.username}
              </div>
            </Typography>
            <Button
              variant="contained"
              color="secondary"
              onClick={async () => {
                await api.Authorization.signOut();
                state.navigate("/sign_in");
              }}
              style={{
                marginLeft: "1em",
                textTransform: "none"
              }}
            >
              <FormattedMessage id="SignOut" defaultMessage="Sign out" />
            </Button>
          </div>
        </Toolbar>
      </AppBar>
    </Box>
    {state.chatTechnology.mode === state.chatTechnology.infiniteList && <MessageUnlimitedList userId={state.userId} username={state.username} />}
    {state.chatTechnology.mode === state.chatTechnology.pagination && <MessagePagination userId={state.userId} username={state.username} />}
    <div className="flex flex-row justify-center items-center w-full" style={{ paddingBottom: "1em", marginTop: "1em" }}>
      <div className="flex flex-auto">
        <TextField
          onPaste={(e) => {
            const files = e.clipboardData.files;
            sendMessageForFileList(files);
          }}
          label={state.intl.formatMessage({
            id: "MessageContent",
            defaultMessage: "Message content"
          })}
          className="flex flex-auto"
          variant="outlined"
          onChange={(e) => {
            state.messageContent = e.target.value;
          }}
          inputProps={{
            style: {
              ...(isMobilePhone ? {} : { resize: "vertical" }),
            }
          }}
          style={{ width: "230px" }}
          value={state.messageContent}
          onKeyDown={(e) => {
            if (isMobilePhone) {
              return;
            }
            if (!e.shiftKey && e.key === "Enter") {
              sendMessage();
              e.preventDefault();
            }
          }}
          autoComplete="off"
          id={state.messageInputId}
          multiline={true}
          rows={isMobilePhone ? 1 : 4}
          ref={state.textareaRef as any}
        />
        {!isMobilePhone && <Button
          variant="contained"
          color={state.messageContent.trim() ? 'primary' : 'secondary'}
          size="large"
          style={{
            marginLeft: "1em",
            textTransform: "none",
            whiteSpace: "nowrap",
          }}
          startIcon={state.loadingOfSend ? <CircularProgress
            size="22px"
            color={state.messageContent.trim() ? "secondary" : "primary"}
          /> : (state.messageContent.trim() ?
            <SendIcon />
            :
            <CloudUploadIcon />
          )}
          onClick={() => {
            if (state.loadingOfSend) {
              return;
            }
            if (state.messageContent.trim()) {
              sendMessage();
            } else {
              state.inputFileRef.current.click();
            }
          }}
        >
          {
            state.messageContent.trim()
              ?
              <FormattedMessage id="Send" defaultMessage="Send" />
              :
              <FormattedMessage id="Upload" defaultMessage="Upload" />
          }
        </Button>}
        {isMobilePhone && <Fab
          onClick={() => {
            if (state.loadingOfSend) {
              return;
            }
            if (state.messageContent.trim()) {
              sendMessage();
            } else {
              state.moreActionDialog.open = true;
            }
          }}
          onMouseDown={(e) => {
            e.preventDefault()
          }}
          onMouseUp={(e) => {
            e.preventDefault()
          }}
          style={{
            marginLeft: "1em",
            textTransform: "none",
            whiteSpace: "nowrap",
          }}
          color={state.messageContent.trim() ? "primary" : "secondary"}
        >
          {state.loadingOfSend ? <CircularProgress
            size="22px"
            color={state.messageContent.trim() ? "secondary" : "primary"}
          /> : (state.messageContent.trim() ?
            <SendIcon style={{ fontSize: "xx-large" }} />
            :
            <AddIcon style={{ fontSize: "xx-large" }} />
          )}
        </Fab>}
      </div>
    </div>
    <input
      type="file"
      hidden={true}
      ref={state.inputFileRef}
      key={state.inputFileId}
      onChange={async (e) => {
        sendMessageForFileList(e.target.files!);
      }}
    />
    {state.moreActionDialog.open && <MessageMoreActionDialog
      closeDialog={() => {
        state.moreActionDialog.open = false;
        state.textareaRef.current?.blur();
        state.textareaRef.current?.focus();
      }}
      uploadFile={() => state.inputFileRef.current.click()}
    />}
  </div>;
})