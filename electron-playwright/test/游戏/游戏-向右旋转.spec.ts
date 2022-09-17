import { ElectronApplication, Page } from 'playwright';
import { timer } from 'rxjs';
import action, { PageType } from '../../src/action'

test('', async () => {
    const 游戏渲染器 = await page.游戏.游戏渲染器();
    await 游戏渲染器.hover({ position: { x: 0, y: 0 } })
    await window.mouse.move(200, 200)
    await window.mouse.down({ button: 'left' })
    await window.mouse.move(220, 200);
    await window.mouse.up({ button: "left" })
    await timer(2000).toPromise();
})

beforeEach(async () => {
    const result = await action.打开程序();
    electron = result.electron;
    window = result.window;
    page = result.page;
    const 进入游戏 = await page.主页.进入游戏();
    await 进入游戏.click()
    const 游戏渲染器 = await page.游戏.游戏渲染器();
    expect(await 游戏渲染器.isVisible()).toBeTruthy();
})

afterEach(async () => {
    await electron.close();
})

let electron: ElectronApplication;
let window: Page;
let page: PageType;