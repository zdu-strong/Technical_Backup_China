import action from '../../src/action'
import { ProgramType } from '../../src/page';

test('', async () => {
  const CurrentCPUUsage = await Program.page.Home.CurrentRandomNumber();
  expect(await CurrentCPUUsage.isVisible()).toBeTruthy()
})

beforeEach(async () => {
  Program = await action.OpenProgram();
})

afterEach(async () => {
  await Program.electron.close();
})

let Program: ProgramType = null as any;