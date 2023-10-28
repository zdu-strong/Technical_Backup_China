import { observer, useMobxState } from "mobx-react-use-autorun";
import { MessageService } from "@/common/MessageService";
import api from '@/api'
import { Button, TextField } from "@mui/material";
import { FormattedMessage } from "react-intl";
import { v1 } from 'uuid'
import { isMobilePhone } from "@/common/is-mobile-phone";
import { concatMap, from, map, timer, toArray } from "rxjs";
import { useRef } from "react";
import MessageMoreActionDialog from "@/component/MessageMoreAction/MessageMoreActionDialog";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCloudArrowUp, faPaperPlane, faPlus, faSpinner } from "@fortawesome/free-solid-svg-icons";

export default observer((props: {
  username: string,
  userId: string,
  variableSizeListRef: React.MutableRefObject<{
    scrollToItemByLast: () => Promise<void>;
  } | undefined>,
}) => {
  const state = useMobxState({
    /* Pending send message */
    messageContent: "",
    /* Is the message being sent */
    loadingOfSend: false,
    inputFileId: v1(),
    messageInputId: v1(),
    textareaRef: useRef<HTMLTextAreaElement>(),
    moreActionDialog: {
      open: false,
    },
  }, {
    ...props,
    inputFileRef: useRef<any>(),
  })

  async function sendMessage() {
    await state.variableSizeListRef.current?.scrollToItemByLast();
    if (!state.messageContent) {
      return MessageService.error("Please fill in the message content");
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
      MessageService.error("Failed to send")
    } finally {
      state.loadingOfSend = false;
    }
  }

  async function sendMessageForFileList(fileList: FileList) {
    await state.variableSizeListRef.current?.scrollToItemByLast();
    if (isMobilePhone) {
      state.moreActionDialog.open = false;
      await timer(1).toPromise();
    }

    state.textareaRef.current?.focus();

    if (state.loadingOfSend) {
      return;
    }

    state.inputFileId = v1();
    if (fileList.length > 0) {
      try {
        state.loadingOfSend = true;
        const urlList = (await from(fileList).pipe(
          concatMap((file) => from(api.Resource.upload(file))),
          map(({ url }) => url),
          toArray(),
        ).toPromise())! as string[];
        for (const url of urlList) {
          await api.UserMessage.sendMessage({
            url,
          });
        }
      } catch (error) {
        MessageService.error("Failed to send")
      } finally {
        state.loadingOfSend = false;
      }
    }
  }

  return <>
    <div className="flex flex-row justify-center items-center w-full" style={{ paddingBottom: "1em", marginTop: "1em" }}>
      <div className="flex flex-auto">
        <TextField
          onPaste={(e) => {
            const files = e.clipboardData.files;
            sendMessageForFileList(files);
          }}
          label={<FormattedMessage id="MessageContent" defaultMessage="Message content" />}
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
              e.preventDefault();
              if (!state.messageContent) {
                return;
              }
              sendMessage();
            }
          }}
          autoComplete="off"
          id={state.messageInputId}
          multiline={true}
          rows={isMobilePhone ? 1 : 4}
          inputRef={state.textareaRef}
          autoFocus={true}
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
          startIcon={<FontAwesomeIcon icon={state.loadingOfSend ? faSpinner : (state.messageContent.trim() ? faPaperPlane : faCloudArrowUp)} spin={state.loadingOfSend} />}
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
        {isMobilePhone && <Button
          variant="contained"
          style={{
            marginLeft: "0.5em",
            textTransform: "none",
            whiteSpace: "nowrap",
          }}
          startIcon={<FontAwesomeIcon icon={state.loadingOfSend ? faSpinner : (state.messageContent.trim() ? faPaperPlane : faPlus)} spin={state.loadingOfSend} />}
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
        >
          {state.messageContent ? "Send" : "More"}
        </Button>}
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
        state.textareaRef.current?.focus();
      }}
      uploadFile={() => state.inputFileRef.current.click()}
    />}
  </>;
})