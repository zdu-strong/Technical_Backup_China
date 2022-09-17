import { Page } from "playwright";

export default (window: Page) => ({
    当前CPU使用率: async () => {
        const xpath = "//*[text()='Current cpu usage']"
        await window.waitForSelector(xpath, { timeout: 36000000 })
        return window.locator(xpath)
    },
    进入游戏: async () => {
        const xpath = `//button[contains(text(),'Enter the game')]`
        await window.waitForSelector(xpath)
        return window.locator(xpath)
    }
})