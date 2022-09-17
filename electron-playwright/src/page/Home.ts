import { Page } from "playwright";

export default (window: Page) => ({
  CurrentCPUUsage: async () => {
    const xpath = "//*[text()='Current cpu usage']"
    await window.waitForSelector(xpath, { timeout: 36000000 })
    return window.locator(xpath)
  },
  EnterTheGame: async () => {
    const xpath = `//button[contains(text(),'Enter the game')]`
    await window.waitForSelector(xpath)
    return window.locator(xpath)
  }
})