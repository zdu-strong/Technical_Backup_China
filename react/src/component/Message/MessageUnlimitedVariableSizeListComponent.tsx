import { observer, useMobxEffect, useMobxState } from "mobx-react-use-autorun";
import { VariableSizeList } from 'react-window'
import { stylesheet } from "typestyle";
import { useImperativeHandle, forwardRef, useRef, ReactNode, Ref } from "react";
import MessageUnlimitedVariableSizeListChildComponent from "./MessageUnlimitedVariableSizeListChildComponent";
import { v1 } from "uuid";
import AutoSizer from 'react-virtualized-auto-sizer'
import { concatMap, delay, EMPTY, fromEvent, interval, ReplaySubject, Subscription, switchMap, take, tap, timer } from "rxjs";
import { useMount, useUnmount } from "mobx-react-use-autorun";
import { DefaultVariableSizeListChildRowHeight } from "./js/DefaultVariableSizeListChildRowHeight";
import { DefaultVariableSizeListAdjustDuration } from "./js/DefaultVariableSizeListAdjustDuration";
import * as mathjs from 'mathjs'

export default observer(forwardRef((props: {
  totalPage: number,
  children: (props: { pageNum: number }) => ReactNode,
  ready: boolean,
}, ref: Ref<{
  scrollToItemByPageNum: (pageNum: number) => void,
  isNeedScrollToEnd: () => boolean,
}>) => {

  const state = useMobxState({
    /* 每一行的高度 */
    rowHeightMap: {

    } as Record<number, number>,

    /* 可见区域之外呈现的行的数量 */
    overscanCount: 0,

    /* 子元素id的前缀 */
    idPrefix: `${v1()}-message-child-`,

    extraScrollOffsetSubject: new ReplaySubject<number>(1),

    extraScrollDate: new Date(),

    extraScrollItem: 0,

    extraScrollItemSubject: new ReplaySubject<number>(1),

    subscription: new Subscription(),

    getRowHeightByIndex: (index: number) => {
      return getRowHeightByPageNum(state.baseTotalPage + index + 1);
    },

    scrollTopOffset: 0,

    /* 是否需要滚动到最后, 仅作一部分参考 */
    isNeedScrollToEndByCaluate: true,
    initTotalPage: 0,
    baseTotalPage: 0,
    css: stylesheet({
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
    }),
  }, {
    ...props,
    ref,
    variableSizeListRef: useRef<VariableSizeList<string[]>>(),
    innerRef: useRef<HTMLDivElement>(),
  })

  useMount(async () => {
    subscribeToExtraScrollItemSubject();
    subscribeToScrollToTheEndWhenResize();
  })

  useUnmount(() => {
    state.subscription.unsubscribe();
  })

  function subscribeToScrollToTheEndWhenResize(){
    state.subscription.add(fromEvent(window,"resize").pipe(
      tap(()=>{
        if(isNeedScrollToEnd()){
          scrollToItemByPageNum(state.totalPage);
        }
      })
    ).subscribe());
  }

  /**
   * 列表模式能够承受大约10万条消息, 所以取往前5万条, 未来5万条, 合10万条进行显示.
   * 当总条数增大或减小, 会自动调整.
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

  /* 获得每行的行高 */
  function getRowHeightByPageNum(pageNum: number) {
    return state.rowHeightMap[pageNum] || DefaultVariableSizeListChildRowHeight;
  }

  /* 为ref添加方法 */
  useImperativeHandle(state.ref, () => ({
    scrollToItemByPageNum,
    isNeedScrollToEnd
  }))

  async function scrollToItemByPageNum(pageNum: number) {
    state.extraScrollItem = pageNum;
    state.extraScrollDate = new Date(new Date().getTime() + DefaultVariableSizeListAdjustDuration);
    state.extraScrollItemSubject.next(pageNum);
  }

  function isNeedScrollToEnd() {
    return state.isNeedScrollToEndByCaluate;
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

  function subscribeToExtraScrollItemSubject() {
    state.subscription.add(state.extraScrollItemSubject.pipe(
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

  function childRender({ height, width }: { height: number; width: number }) {
    return <VariableSizeList
      height={height}
      itemCount={state.totalPage - state.baseTotalPage}
      itemSize={state.getRowHeightByIndex}
      width={width}
      ref={state.variableSizeListRef as any}
      className={state.css.VariableSizeList}
      innerRef={state.innerRef}
      /* 要在可见区域之外呈现的行的数量 */
      overscanCount={state.overscanCount}
      style={state.ready ? {} : { visibility: "hidden" }}
      onScroll={({ scrollOffset }) => {
        state.scrollTopOffset = scrollOffset;
        calcualateIsNeedScrollToEnd(height);
      }}
      key={state.baseTotalPage}
    >
      {/* 子元素 */}
      {(props) => <MessageUnlimitedVariableSizeListChildComponent
        {...props}
        /* 设置当前元素的高度 */
        setRowHeight={async (rowHeight) => {
          var pageNum = state.baseTotalPage + props.index + 1;
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

  return <div className={state.css.autoSizer}>
    <AutoSizer>
      {childRender}
    </AutoSizer>
  </div>
}))