import { observer, useMobxEffect, useMobxState } from "mobx-react-use-autorun";
import { VariableSizeList } from 'react-window'
import { stylesheet } from "typestyle";
import { useImperativeHandle, forwardRef, useRef, ReactNode, Ref } from "react";
import MessageUnlimitedVariableSizeListChildComponent from "@/component/Message/MessageUnlimitedVariableSizeListChildComponent";
import { v1 } from "uuid";
import AutoSizer, { Size } from 'react-virtualized-auto-sizer'
import { concatMap, delay, EMPTY, fromEvent, interval, ReplaySubject, Subscription, switchMap, take, tap, timer } from "rxjs";
import { useMount } from "mobx-react-use-autorun";
import { DefaultVariableSizeListChildRowHeight } from "@/component/Message/js/DefaultVariableSizeListChildRowHeight";
import { DefaultVariableSizeListAdjustDuration } from "@/component/Message/js/DefaultVariableSizeListAdjustDuration";
import * as mathjs from 'mathjs'

const css = stylesheet({
  VariableSizeList: {
    border: "1px solid purple",
    scrollbarWidth: "none",
    $nest: {
      "&::-webkit-scrollbar": {
        display: "none",
      }
    }
  },
  autoSizer: {
    display: "flex",
    flexDirection: "row",
    flex: "1 1 auto",
    height: "100%",
    width: "100%",
    minHeight: "100px",
  }
})

export default observer(forwardRef((props: {
  totalPage: number,
  children: (props: { pageNum: number }) => ReactNode,
  ready: boolean,
}, ref: Ref<{
  scrollToItemByPageNum: (pageNum: number) => void,
  isNeedScrollToEnd: () => boolean,
  scrollToItemByLast: () => Promise<void>;
}>) => {

  const state = useMobxState({
    /* the height of each row */
    rowHeightMap: {

    } as Record<number, number>,

    /* The number of lines rendered outside the visible area */
    overscanCount: 0,

    /* The prefix of the child element id */
    idPrefix: `${v1()}-message-child-`,

    extraScrollOffsetSubject: new ReplaySubject<number>(1),

    extraScrollDate: new Date(),

    extraScrollItem: 0,

    extraScrollItemSubject: new ReplaySubject<number>(1),

    getRowHeightByIndex: (index: number) => {
      return getRowHeightByPageNum(state.baseTotalPage + index + 1);
    },

    scrollTopOffset: 0,

    /* Is need to scroll to the end, just for a part of reference */
    isNeedScrollToEndByCaluate: true,
    initTotalPage: 0,
    baseTotalPage: 0,

  }, {
    ...props,
    ref,
    variableSizeListRef: useRef<VariableSizeList<string[]>>(),
    innerRef: useRef<HTMLDivElement>(),
  })

  useMount((subscription) => {
    subscribeToExtraScrollItemSubject(subscription);
    subscribeToScrollToTheEndWhenResize(subscription);
  })

  function subscribeToScrollToTheEndWhenResize(subscription: Subscription) {
    subscription.add(fromEvent(window, "resize").pipe(
      tap(() => {
        if (isNeedScrollToEnd()) {
          scrollToItemByPageNum(state.totalPage);
        }
      })
    ).subscribe());
  }

  /**
   * The list mode can bear about 100,000 messages, so the previous 50,000 messages, the next 50,000 messages, and 100,000 messages are displayed together.
   * When the total number increases or decreases, it will be adjusted automatically.
   */
  useMobxEffect(() => {
    if (!state.initTotalPage) {
      state.initTotalPage = state.totalPage;
    }
    state.baseTotalPage = calculateBaseTotalNumber(state.initTotalPage, state.totalPage);

  }, [state.totalPage]);

  function calculateBaseTotalNumber(initTotalPage: number, totalPage: number) {
    const half = 50000;
    const remainderOfInitTotalPage = mathjs.mod(initTotalPage, half);
    let baseTotalPage = 0;
    if (totalPage >= initTotalPage) {
      baseTotalPage = remainderOfInitTotalPage + mathjs.multiply((Math.floor(mathjs.divide(totalPage - remainderOfInitTotalPage, half) - 1)), half);
      if (baseTotalPage < 0) {
        baseTotalPage = 0;
      }
    } else {
      baseTotalPage = remainderOfInitTotalPage + mathjs.multiply((Math.ceil(mathjs.divide(totalPage - remainderOfInitTotalPage, half) - 1)), half);
      if (baseTotalPage < 0) {
        baseTotalPage = 0;
      }
    }

    return baseTotalPage;
  }

  /* Get the row height of each row */
  function getRowHeightByPageNum(pageNum: number) {
    return state.rowHeightMap[pageNum] || DefaultVariableSizeListChildRowHeight;
  }

  /* Add method for ref */
  useImperativeHandle(state.ref, () => ({
    scrollToItemByPageNum,
    isNeedScrollToEnd,
    scrollToItemByLast,
  }))

  async function scrollToItemByPageNum(pageNum: number) {
    state.extraScrollItem = pageNum;
    state.extraScrollDate = new Date(new Date().getTime() + DefaultVariableSizeListAdjustDuration);
    state.extraScrollItemSubject.next(pageNum);
  }

  function isNeedScrollToEnd() {
    return state.isNeedScrollToEndByCaluate;
  }

  async function scrollToItemByLast() {
    await scrollToItemByPageNum(state.totalPage);
  }

  function calcualateIsNeedScrollToEnd(height: number) {
    if (!state.totalPage) {
      state.isNeedScrollToEndByCaluate = true;
      return;
    }

    const itemElements = state.innerRef.current?.getElementsByClassName(`${state.idPrefix}-${state.totalPage}`);
    if (itemElements?.length) {
      const itemTopString = (itemElements[0] as any).style.top as string;
      const itemTopNumber = Number(itemTopString.slice(0, itemTopString.length - 2));
      const topOffsetOfLastItem = itemTopNumber;
      const mistakeNumber = 10;
      state.isNeedScrollToEndByCaluate =
        (
          state.scrollTopOffset + height + mistakeNumber
          >=
          topOffsetOfLastItem + getRowHeightByPageNum(state.totalPage)
        )
        ||
        (
          (
            state.scrollTopOffset + mistakeNumber
            >=
            topOffsetOfLastItem
          )
          &&
          (
            state.scrollTopOffset - mistakeNumber
            <=
            topOffsetOfLastItem
          )
        );
    } else {
      state.isNeedScrollToEndByCaluate = false;
    }
  }

  function calcualateIsNeedAdjustScrollOffsetInOrderToScrollUpAndTheHeadOfTheContentIsAboveTheVisibleAreaByPageNum(pageNum: number) {
    const itemElements = state.innerRef.current?.getElementsByClassName(`${state.idPrefix}-${pageNum}`);
    if (itemElements?.length) {
      const itemTopString = (itemElements[0] as any).style.top as string;
      const itemTopNumber = Number(itemTopString.slice(0, itemTopString.length - 2));
      const topOffsetOfLastItem = itemTopNumber;
      if (topOffsetOfLastItem < state.scrollTopOffset) {
        return true;
      }
    }
    return false;
  }

  function subscribeToExtraScrollItemSubject(subscription: Subscription) {
    subscription.add(state.extraScrollItemSubject.pipe(
      switchMap((pageNum) => {
        return interval(1).pipe(
          take(200),
          concatMap(() => {
            state.variableSizeListRef?.current?.scrollToItem(pageNum - 1 - state.baseTotalPage, "start");
            const itemElements = state.innerRef.current?.getElementsByClassName(`${state.idPrefix}-${pageNum}`);
            if (itemElements?.length) {
              return timer(1).pipe(
                tap(() => {
                  state.variableSizeListRef?.current?.scrollToItem(pageNum - 1 - state.baseTotalPage, "start");
                }),
                delay(1),
                tap(() => {
                  state.variableSizeListRef?.current?.scrollToItem(pageNum - 1 - state.baseTotalPage, "start");
                }),
              );
            } else {
              return EMPTY;
            }
          }),
          take(1),
        )
      })
    ).subscribe());
  }

  function childRender(size: Size) {
    return <VariableSizeList
      height={size.height!}
      itemCount={state.totalPage - state.baseTotalPage}
      itemSize={state.getRowHeightByIndex}
      width={size.width!}
      ref={state.variableSizeListRef as any}
      className={css.VariableSizeList}
      innerRef={state.innerRef}
      /* The number of rows to render outside the visible area */
      overscanCount={state.overscanCount}
      style={state.ready ? {} : { visibility: "hidden" }}
      onScroll={({ scrollOffset }) => {
        state.scrollTopOffset = scrollOffset;
        calcualateIsNeedScrollToEnd(size.height!);
      }}
      key={state.baseTotalPage}
    >
      {/* child element */}
      {(props) => <MessageUnlimitedVariableSizeListChildComponent
        {...props}
        /* Set the height of the current element */
        setRowHeight={async (rowHeight) => {
          const pageNum = state.baseTotalPage + props.index + 1;
          const originHeight = getRowHeightByPageNum(pageNum);
          if (!(rowHeight !== originHeight && rowHeight >= DefaultVariableSizeListChildRowHeight)) {
            return;
          }

          state.rowHeightMap[pageNum] = rowHeight;
          state.variableSizeListRef?.current?.resetAfterIndex(props.index);

          if (isNeedScrollToEnd() && new Date().getTime() < state.extraScrollDate.getTime() && state.extraScrollItem) {
            scrollToItemByPageNum(state.extraScrollItem);
            return;
          }

          if (calcualateIsNeedAdjustScrollOffsetInOrderToScrollUpAndTheHeadOfTheContentIsAboveTheVisibleAreaByPageNum(pageNum)) {
            state.variableSizeListRef.current?.scrollTo(state.scrollTopOffset + rowHeight - originHeight);
            return;
          }
        }}
        idPrefix={state.idPrefix}
        baseTotalPage={state.baseTotalPage}
        pageNum={state.baseTotalPage + props.index + 1}
      >
        {state.children}
      </MessageUnlimitedVariableSizeListChildComponent>}
    </VariableSizeList>;
  }

  return <div className={css.autoSizer}>
    <AutoSizer>
      {childRender}
    </AutoSizer>
  </div>
}))