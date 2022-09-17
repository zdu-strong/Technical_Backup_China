import { ElectronApplication, Page } from 'playwright';
import { timer } from 'rxjs';
import action, { PageType } from '../../src/action'

test('', async () => {
    const 游戏渲染器 = await page.游戏.游戏渲染器();
    expect(await 游戏渲染器.isVisible()).toBeTruthy();
    await timer(2000).toPromise();
})

beforeEach(async () => {
    const result = await action.打开程序();
    electron = result.electron;
    window = result.window;
    page = result.page;
    const 进入游戏 = await page.主页.进入游戏();
    await 进入游戏.click()
})

afterEach(async () => {
    await electron.close();
})

let electron: ElectronApplication;
let window: Page;
let page: PageType;