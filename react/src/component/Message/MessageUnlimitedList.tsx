import { observer, useMobxEffect, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import api from '@/api'
import { useMount } from "mobx-react-use-autorun";
import { concatMap, from, catchError, Observable, switchMap, timer, repeat, ReplaySubject, tap, Subscription } from 'rxjs'
import { useRef } from "react";
import MessageUnlimitedListChild from "./MessageUnlimitedListChild";
import MessageUnlimitedVariableSizeListComponent from "./MessageUnlimitedVariableSizeListComponent";
import { Alert, CircularProgress } from "@mui/material";
import { FormattedMessage } from "react-intl";
import { UserMessageModel } from "@/model/UserMessageModel";

const css = stylesheet({
  messsageListContainer: {
    width: "100%",
    height: "100%",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    flex: "1 1 auto",
  },
})

export default observer((props: {
  userId: string,
  username: string,
  setReadyForMessageList: (readyForMessageList: boolean) => Promise<void>,
}) => {

  const state = useMobxState({
    /* Which item you want to jump to, the value of the input box */
    jumpItemOfInput: "",
    /* total number of messages */
    totalPage: 0,
    /* message websocket */
    websocketInput: new ReplaySubject<{
      pageNum: number,
      isCancel: boolean,
    }>(1000),
    /* Is ready */
    ready: false,
    /* Is there an error */
    error: null as boolean | null,
    messageMap: {} as Record<string, UserMessageModel>,
    listenMessageSet: new Set<number>(),
    child,
  }, {
    ...props,
    variableSizeListRef: useRef<{
      scrollToItemByPageNum: (pageNum: number) => Promise<void>,
      isNeedScrollToEnd: () => boolean,
    }>(),
  })

  useMount((subscription) => {
    loadAllMessage(subscription);
  })

  function child(props: { pageNum: number }) {
    return <MessageUnlimitedListChild
      loadMessage={() => {
        state.listenMessageSet.add(props.pageNum);
        state.websocketInput.next({
          pageNum: props.pageNum,
          isCancel: false,
        });
      }}
      unloadMessage={() => {
        state.listenMessageSet.delete(props.pageNum);
        state.websocketInput.next({
          pageNum: props.pageNum,
          isCancel: true,
        });
      }}
      pageNum={props.pageNum}
      ready={!!state.messageMap[props.pageNum]}
      loading={!state.messageMap[props.pageNum]}
      message={state.messageMap[props.pageNum]}
      userId={state.userId}
      username={state.username}
    />
  }

  useMobxEffect(() => {
    state.child = child;
  })

  function loadAllMessage(subscription: Subscription) {
    subscription.add(new Observable<null>((subscriber) => {
      subscriber.next();
      subscriber.complete();
    }).pipe(
      switchMap(() => api.UserMessage.getUserMessageWebsocket(state.websocketInput)),
      concatMap(({ list: messageList, totalPage }) => from((async () => {
        if (typeof totalPage === "number") {
          state.totalPage = totalPage;
        }
        for (const message of messageList) {
          state.messageMap[message.pageNum] = message;
        }
        let isNeedScrollToEnd: boolean | undefined = state.variableSizeListRef.current?.isNeedScrollToEnd();

        if (!state.ready) {
          isNeedScrollToEnd = true;
        }
        if (isNeedScrollToEnd) {
          await state.variableSizeListRef.current?.scrollToItemByPageNum(state.totalPage);
        }
        state.ready = true;
        state.error = null;
        await state.setReadyForMessageList(true);
      })())),
      tap(() => {
        cleanMessageMap();
      }),
      repeat(),
      catchError((error, caught) => {
        if (!state.ready) {
          state.error = true;
        }
        return timer(1000).pipe(
          switchMap(() => caught),
        );
      }),
    ).subscribe());
  }

  function cleanMessageMap() {
    for (const pageNum in state.messageMap) {
      if (Number(pageNum) > state.totalPage - 100) {
        continue;
      }
      if (state.listenMessageSet.has(Number(pageNum))) {
        continue;
      }

      if (Array.from(state.listenMessageSet).some(s => s - 100 < Number(pageNum) && s + 100 > Number(pageNum))) {
        continue;
      }

      delete state.messageMap[pageNum];
    }
  }

  return <div className={css.messsageListContainer}>
    {!state.error && !state.ready && <CircularProgress style={{ width: "40px", height: "40px" }} />}
    {state.error && <Alert severity="error">
      <FormattedMessage id="ServerAccessError" defaultMessage="Server access error" />
    </Alert>}
    <MessageUnlimitedVariableSizeListComponent totalPage={state.totalPage} ref={state.variableSizeListRef as any} ready={state.ready && !state.error}>
      {state.child}
    </MessageUnlimitedVariableSizeListComponent>
  </div>;
})