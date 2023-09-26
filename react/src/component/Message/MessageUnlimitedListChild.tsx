import { observer, useMobxState } from "mobx-react-use-autorun";
import { useMount } from "mobx-react-use-autorun";
import { Button, Chip, CircularProgress, Divider, Link } from "@mui/material";
import api from '@/api'
import { MessageService } from "@/common/MessageService";
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import { FormattedMessage } from "react-intl";
import { UserMessageModel } from "@/model/UserMessageModel";
import path from 'path'
import DescriptionOutlinedIcon from '@mui/icons-material/DescriptionOutlined';
import { Subscription } from "rxjs";
import { DefaultVariableSizeListChildRowHeight } from "./js/DefaultVariableSizeListChildRowHeight";

export default observer((props: {
  loadMessage: () => void,
  unloadMessage: () => void,
  pageNum: number,
  ready: boolean,
  loading: boolean,
  message: UserMessageModel,
  userId: string,
  username: string,
  idPrefix: string,
}) => {

  const state = useMobxState({
    loadingOfRecall: false,
  }, {
    ...props,
  })

  useMount((subscription) => {
    subscription.add(new Subscription(() => {
      state.unloadMessage();
    }))
    state.loadMessage();
  })

  async function recall() {
    try {
      state.loadingOfRecall = true
      await api.UserMessage.recallMessage(state.message?.id!);
    } catch (error) {
      state.loadingOfRecall = false
      MessageService.error("Failed to withdraw");
    }
  }

  return <div className="flex flex-col flex-auto w-full justify-between" style={{ minHeight: `${DefaultVariableSizeListChildRowHeight}px` }} id={`${state.idPrefix}${state.pageNum}`}>
    <div className="flex flex-col flex-auto w-full">
      <div className="flex flex-row flex-wrap w-full justify-between" style={{ whiteSpace: "pre-wrap", wordBreak: "break-word", wordWrap: "break-word" }}>
        <div className="flex">
          {state.loading && <FormattedMessage
            id="LineFirstLoading"
            defaultMessage="Line {lineNumber}, Loading..."
            values={{ lineNumber: state.pageNum }}
          />}
          {state.ready && state.message.user.id !== state.userId && <FormattedMessage
            id="LineFirstUserSpeaking"
            defaultMessage="Line {lineNumber}, {username}: "
            values={{
              lineNumber: state.pageNum,
              username: state.message.user.username
            }}
          />}
          {state.ready && state.message.user.id === state.userId && <FormattedMessage
            id="LineFirstMeSpeaking"
            defaultMessage="Line {lineNumber}, Me:"
            values={{ lineNumber: state.pageNum }}
          />}
        </div>
        {state.ready && !state.message?.isRecall && state.message?.user.id === state.userId && <div>
          <Button
            startIcon={state.loadingOfRecall ? <CircularProgress size="12px" /> : <VisibilityOffIcon style={{ fontSize: "12px" }} />}
            size="small" variant="outlined"
            style={{ marginRight: "3px", marginTop: "3px", textTransform: "none", lineHeight: "14px" }}
            onClick={recall}
            onMouseDown={(e) => e.preventDefault()}
          >
            <FormattedMessage id="Withdrawn" defaultMessage="Withdrawn" />
          </Button>
        </div>}
      </div>
      <div className="flex">
        {state.ready && state.message?.isRecall && <Chip
          label={<FormattedMessage id="RetractedAMessage" defaultMessage="retracted a message" />}
          variant="outlined"
          style={{ marginBottom: "0.5em", marginTop: "0.5em" }}
          size="small"
        />}
      </div>
      <div className="flex" style={{ whiteSpace: "pre-wrap", wordBreak: "break-word", overflowWrap: "break-word" }}>
        {state.ready && !state.message?.isRecall && <>
          {state.message?.content || ""}
        </>}
      </div>
      {state.ready && !state.message?.isRecall && state.message?.url && <div className="flex" style={{ whiteSpace: "pre-wrap", wordBreak: "break-word", overflowWrap: "break-word" }}>
        <div className="flex">
          <div className="flex" style={{ marginRight: "1em", whiteSpace: "nowrap" }}>
            <FormattedMessage id="Download" defaultMessage="Download" />
          </div>
          <Link
            href={state.message?.url}
            download
            className="flex items-start"
            style={{
              whiteSpace: "pre-wrap",
              wordBreak: "break-word",
              overflowWrap: "break-word",
            }}
          >
            <DescriptionOutlinedIcon style={{ fontSize: "1em", marginTop: "0.2em" }} />
            {`${decodeURIComponent(path.basename(state.message?.url))}`}
          </Link>
        </div>
      </div>}
    </div>
    <Divider />
  </div>
})
