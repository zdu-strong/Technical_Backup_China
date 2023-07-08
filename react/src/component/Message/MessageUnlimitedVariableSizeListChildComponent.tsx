import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { ReactNode, useRef } from "react";
import { useMount } from "mobx-react-use-autorun";
import { Subscription, tap, timer } from 'rxjs'
import { DefaultVariableSizeListChildRowHeight } from "./js/DefaultVariableSizeListChildRowHeight";

export default observer((props: {
  setRowHeight: (rowHeight: number, topOffset: number) => void,
  children: (props: { pageNum: number }) => ReactNode,
  style: any,
  idPrefix: string,
  baseTotalPage: number,
  pageNum: number,
}) => {
  const state = useMobxState({
    css: stylesheet({
      container: {
        width: "100%",
        display: "flex",
        flexDirection: "column",
        flex: "1 1 auto",
        minHeight: `${DefaultVariableSizeListChildRowHeight}px`,
      },
    }),
  }, {
    ...props,
    containerRef: useRef<any>(),
  })

  function autoChangeSize(subscription: Subscription) {
    const resizeObserver = new ResizeObserver((entries) => {
      subscription.add(timer(1).pipe(
        tap(() => {
          const topOffset = state.style.top;
          state.setRowHeight(entries[0].contentRect.height, topOffset);
        })
      ).subscribe());
    });
    subscription.add(new Subscription(() => {
      resizeObserver.disconnect();
    }));
    resizeObserver.observe(state.containerRef.current);
  }

  function initSetRowHeight() {
    const rowHeight = state.containerRef.current?.clientHeight;
    if (typeof rowHeight === "number") {
      const topOffset = state.style.top;
      state.setRowHeight(rowHeight, topOffset);
    }
  }

  useMount((subscription) => {
    autoChangeSize(subscription);
    initSetRowHeight();
  })

  return <div style={state.style} className={`${state.idPrefix}-${state.pageNum}`}>
    <div className={state.css.container} ref={state.containerRef}>
      {state.children({ pageNum: state.pageNum })}
    </div>
  </div>
})
