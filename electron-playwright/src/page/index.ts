import { Page } from "playwright";
import 主页 from "./主页";
import 游戏 from './游戏'

export default (window: Page) => ({
    主页: 主页(window),
    游戏: 游戏(window),
})