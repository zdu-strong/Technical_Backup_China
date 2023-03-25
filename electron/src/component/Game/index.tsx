import { stylesheet } from 'typestyle';
import { observer, useMobxState } from 'mobx-react-use-autorun';
import { useRef } from 'react';
import { useMount, useUnmount } from "mobx-react-use-autorun"
import { concat, concatMap, delay, EMPTY, fromEvent, interval, of, retry, Subscription, take, tap, timer } from 'rxjs';
import { initGameEngine } from './js/initGameEngine';
import { exhaustMapWithTrailing } from 'rxjs-exhaustmap-with-trailing'
import LoadingOrErrorComponent from '@/common/MessageService/LoadingOrErrorComponent';
import * as BABYLON from '@babylonjs/core'

export default observer(() => {

  const state = useMobxState({
    engine: null as BABYLON.Engine | null,
    subscription: new Subscription(),
    ready: false,
    error: null as any,
    css: stylesheet({
      div: {
        display: "flex",
        flex: "1 1 auto",
        flexDirection: "column",
        height: "100vh",
        $nest: {
          "& > canvas": {
            width: "100%",
            height: "100%",
          },
          "& > div": {
            position: "absolute",
            top: 0,
            left: 0,
            width: "100%",
            height: "100%",
            zIndex: 1000,
            backgroundColor: "white",
          }
        }
      }
    }),
  }, {
    canvasRef: useRef<HTMLCanvasElement>(),
  })

  function resizeGameCanvas() {
    state.subscription.add(concat(of(null), fromEvent(window, "resize")).pipe(
      exhaustMapWithTrailing(() => concat(of(null), interval(1)).pipe(
        concatMap(() => {
          if (state.error) {
            throw state.error;
          }
          if (state.engine) {
            return of(null);
          }
          return EMPTY;
        }),
        take(1),
        tap(() => {
          state.engine?.resize();
        }),
        delay(16),
      )),
      retry(),
    ).subscribe())
  }

  useMount(async () => {
    try {
      for (let i = 10; i > 0; i--) {
        await timer(1).toPromise();
      }
      state.engine = await initGameEngine(state.canvasRef);
      state.subscription.add(new Subscription(() => {
        state.engine?.dispose();
      }));
      resizeGameCanvas();
      for (let i = 10; i > 0; i--) {
        await timer(16).toPromise();
      }
      state.canvasRef.current!.focus();
      state.ready = true;
    } catch (error) {
      state.error = error
    }
  })

  useUnmount(() => {
    state.subscription.unsubscribe()
  })

  return <>
    <div className={state.css.div} style={state.ready ? {} : { position: "relative" }}>
      <canvas ref={state.canvasRef as any} style={{ outlineStyle: "none" }} />
      {!state.ready && <LoadingOrErrorComponent ready={state.ready} error={state.error} />}
    </div>
  </>
})