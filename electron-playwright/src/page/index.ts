import { ElectronApplication, Page } from "playwright";
import Home from "./Home";
import Game from './Game'

function PageClassFunction(window: Page) {
  return {
    Home: Home(window),
    Game: Game(window),
  }
}

export default PageClassFunction;

const PageClassType = PageClassFunction(null as any)
type PageType = typeof PageClassType;
export type ProgramType = {
  electron: ElectronApplication; window: Page; page: PageType;
}