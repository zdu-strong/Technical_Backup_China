import { ElectronApplication, Page } from 'playwright';
import action, { PageType } from '../../src/action'

test('', async () => {
    const 当前CPU使用率 = await page.主页.当前CPU使用率();
    expect(await 当前CPU使用率.isVisible()).toBeTruthy()
})

beforeEach(async () => {
    const result = await action.打开程序();
    electron = result.electron;
    window = result.window;
    page = result.page;
})

afterEach(async () => {
    await electron.close();
})

let electron: ElectronApplication;
let window: Page;
let page: PageType;