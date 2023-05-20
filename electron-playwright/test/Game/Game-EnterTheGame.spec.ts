import { timer } from 'rxjs';
import action from '../../src/action'
import { ProgramType } from '../../src/page';

test('', async () => {
  const GameRenderer = await Program.page.Game.GameRenderer();
  expect(await GameRenderer.isVisible()).toBeTruthy();
  await timer(2000).toPromise();
})

beforeEach(async () => {
  Program = await action.OpenProgram();
  const EnterTheGame = await Program.page.Home.EnterTheGame();
  await EnterTheGame.click()
})

afterEach(async () => {
  await Program.electron.close();
})

let Program!: ProgramType;