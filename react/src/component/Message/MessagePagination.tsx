import { observer, useMobxEffect, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import api from '@/api'
import { useMount, useUnmount } from "mobx-react-use-autorun";
import { Subscription, concatMap, from, catchError, switchMap, timer, repeat, ReplaySubject, tap, of } from 'rxjs'
import { useRef } from "react";
import MessageUnlimitedListChild from "./MessageUnlimitedListChild";
import { Alert, Button, CircularProgress } from "@mui/material";
import { FormattedMessage, useIntl } from "react-intl";
import MessagePaginationVariableSizeListComponent from "./MessagePaginationVariableSizeListComponent";
import * as mathjs from 'mathjs'
import { UserMessageModel } from "@/model/UserMessageModel";

export default observer((props: { userId: string, username: string }) => {
  const state = useMobxState({
    /* 想要跳转到哪一项, 输入框的值 */
    jumpItemOfInput: "",
    /* 消息的总条数 */
    totalPage: 0,
    /* 消息的websocket */
    websocketInput: new ReplaySubject<{
      pageNum: number,
      isCancel: boolean,
    }>(1000),
    /* 订阅 */
    subscription: new Subscription(),
    /* 是否准备好了 */
    ready: false,
    /* 是否有错误 */
    error: null as boolean | null,
    messageMap: {} as Record<string, UserMessageModel>,
    listenMessageSet: new Set<number>(),
    child,
    paginationTotalPage: 0,
    paginationPageNum: 1,
    paginationPageSize: 100,
    css: stylesheet({
      messsageListContainer: {
        width: "100%",
        height: "100%",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        flex: "1 1 auto",
      },
      paginationButtonParentDiv: {
        display: "flex",
        flexDirection: "row",
        flexWrap: "nowrap",
        marginTop: "0.5em",
        justifyContent: "space-between",
        width: "100%",
      }
    })
  }, {
    ...props,
    variableSizeListRef: useRef<{
      scrollToItemByPageNum: (pageNum: number) => Promise<void>,
      isNeedScrollToEnd: () => boolean,
    }>(),
    intl: useIntl(),
  })

  function child(props: { pageNum: number }) {
    return <MessageUnlimitedListChild
      loadMessage={() => null}
      unloadMessage={() => null}
      pageNum={props.pageNum}
      ready={!!state.messageMap[props.pageNum]}
      loading={!state.messageMap[props.pageNum]}
      message={state.messageMap[props.pageNum]}
      userId={state.userId}
      username={state.username}
    />
  }

  useMobxEffect(() => {
    if (state.paginationPageNum > 1 && state.paginationTotalPage < state.paginationPageNum) {
      state.paginationPageNum = state.paginationTotalPage || 1;
    }
  }, [state.paginationPageNum, state.paginationTotalPage]);

  useMount(() => {
    loadAllMessage();
  })

  useUnmount(() => {
    state.subscription.unsubscribe();
  })

  async function loadAllMessage() {
    state.subscription.add(of(null).pipe(
      switchMap(() => api.UserMessage.getUserMessageWebsocket(state.websocketInput)),
      concatMap(({ list: messageList, totalPage }) => from((async () => {
        if (totalPage !== null) {
          const isLastPage = state.paginationPageNum === state.paginationTotalPage;
          state.totalPage = totalPage;
          state.paginationTotalPage = mathjs.ceil(mathjs.divide(totalPage, state.paginationPageSize));
          if ((!state.ready || isLastPage) && state.paginationTotalPage > 0) {
            state.paginationPageNum = state.paginationTotalPage;
          }
        }
        for (const message of messageList) {
          state.messageMap[message.pageNum] = message;
        }
        let isNeedScrollToEnd: boolean | undefined = state.variableSizeListRef.current?.isNeedScrollToEnd();

        if (isNeedScrollToEnd && state.paginationPageNum === state.paginationTotalPage) {
          await state.variableSizeListRef.current?.scrollToItemByPageNum(state.totalPage);
        }
        state.ready = true;
        state.error = null;
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

  return <div className={state.css.messsageListContainer}>
    {!state.error && !state.ready && <CircularProgress style={{ width: "40px", height: "40px" }} />}
    {state.error && <Alert severity="error">
      <FormattedMessage id="ServerAccessError" defaultMessage="Server access error" />
    </Alert>}
    <MessagePaginationVariableSizeListComponent
      key={state.paginationPageNum}
      paginationPageNum={state.paginationPageNum}
      paginationPageSize={state.paginationPageSize}
      totalPage={state.totalPage}
      ref={state.variableSizeListRef as any}
      ready={state.ready && !state.error}
      loadMessage={(pageNum: number) => {
        state.listenMessageSet.add(pageNum);
        state.websocketInput.next({
          pageNum,
          isCancel: false,
        });
      }}
      unloadMessage={(pageNum: number) => {
        state.listenMessageSet.delete(pageNum);
        state.websocketInput.next({
          pageNum,
          isCancel: true,
        });
      }}
    >
      {state.child}
    </MessagePaginationVariableSizeListComponent>
    <div className={state.css.paginationButtonParentDiv}>
      <Button
        variant="outlined"
        style={{
          marginRight: "1em",
          textTransform: "none"
        }}
        onClick={() => {
          if (state.paginationPageNum > 1) {
            state.paginationPageNum--;
          }
        }}
      >
        <FormattedMessage id="PreviousPage" defaultMessage="Previous" />
      </Button>
      <Button
        variant="outlined"
        style={{
          textTransform: "none"
        }}
      >
        {`${state.paginationPageNum || 1}/${state.paginationTotalPage || 1}`}
      </Button>
      <Button
        variant="outlined"
        style={{
          textTransform: "none"
        }}
        onClick={() => {
          if (state.paginationPageNum < state.paginationTotalPage) {
            state.paginationPageNum++;
          }
        }}
      >
        <FormattedMessage id="NextPage" defaultMessage="Next" />
      </Button>
    </div>
  </div >;
})