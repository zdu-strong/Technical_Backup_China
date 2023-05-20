import { timer } from 'rxjs';
import action from '../../src/action'
import { ProgramType } from '../../src/page';

test('', async () => {
  const GameRenderer = await Program.page.Game.GameRenderer();
  await GameRenderer.hover({ position: { x: 0, y: 0 } })
  await Program.window.mouse.move(200, 200)
  await Program.window.mouse.down({ button: 'left' })
  await Program.window.mouse.move(220, 200);
  await Program.window.mouse.up({ button: "left" })
  await timer(2000).toPromise();
})

beforeEach(async () => {
  Program = await action.OpenProgram();
  const EnterTheGame = await Program.page.Home.EnterTheGame();
  await EnterTheGame.click()
  const GameRenderer = await Program.page.Game.GameRenderer();
  expect(await GameRenderer.isVisible()).toBeTruthy();
})

afterEach(async () => {
  await Program.electron.close();
})

let Program!: ProgramType;