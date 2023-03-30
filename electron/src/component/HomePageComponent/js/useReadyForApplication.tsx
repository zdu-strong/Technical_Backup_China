import { observable } from 'mobx-react-use-autorun';
import { useMount } from "mobx-react-use-autorun";
import { timer } from 'rxjs';
import remote from '@/remote';
import { MessageService } from '@/common/MessageService';

const state = observable({
  ready: false,
})

export const useReadyForApplication = () => {

  useMount(async () => {
    if (!state.ready) {
      const isLoaded = await remote.getIsLoaded();
      if (!isLoaded) {
        const display = remote.screen.getDisplayNearestPoint(remote.screen.getCursorScreenPoint());
        const defaultWidth = display.workArea.width / 2;
        const defaultHeight = display.workArea.height / 2;
        const width = display.workArea.width > defaultWidth ? defaultWidth : display.workArea.width;
        const height = display.workArea.height > defaultHeight ? defaultHeight : display.workArea.height;
        remote.getCurrentWindow().setBounds({
          width: Math.floor(width),
          height: Math.floor(height),
          x: Math.floor(display.workArea.x + ((display.workArea.width - width) / 2)),
          y: Math.floor(display.workArea.y + ((display.workArea.height - height) / 2)),
        });
        if (!remote.isNotShowForTest) {
          remote.getCurrentWindow().maximize();
        }
        if (!remote.isNotShowForTest) {
          remote.getCurrentWindow().show();
        }
        remote.getCurrentWindow().setAlwaysOnTop(true, "status");
        remote.getCurrentWindow().focus();
        if (!remote.isNotShowForTest) {
          remote.getCurrentWindow().moveTop();
        }
        remote.getCurrentWindow().setAlwaysOnTop(false);
        remote.getCurrentWindow().setMenuBarVisibility(true);
        remote.getCurrentWindow().setTitle("React App");
        for (let i = 10; i > 0; i--) {
          await timer(1).toPromise();
        }
        remote.StorageManageRunUtil().catch((error) => MessageService.error(error));
        for (let i = 10; i > 0; i--) {
          await timer(1).toPromise();
        }
        await remote.setIsLoadedToTrue();
      }
    }
    state.ready = true
  })

  return state.ready;
}