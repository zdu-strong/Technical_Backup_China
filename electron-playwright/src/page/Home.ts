import { Page } from "playwright";

export default (window: Page) => ({
  CurrentRandomNumber: async () => {
    const xpath = "//div[contains(@class, 'batteryContainer')]/div/div[contains(., 'Random number')]"
    await window.waitForSelector(xpath, { timeout: 36000000 })
    return window.locator(xpath)
  },
  EnterTheGame: async () => {
    const xpath = `//button[contains(.,'Enter the game')]`
    await window.waitForSelector(xpath)
    return window.locator(xpath)
  }
})