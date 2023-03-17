import page from '../../page'
import { v1 } from 'uuid'
import * as action from '../../action'

it('', () => {
  page.Chat.RecallMessageButton(message).click()
  page.Chat.Message(message).should("not.exist")
})

before(() => {
  cy.visit("/sign_up")
  action.signUp(email, password)
  page.Chat.MessageContentInput().type(message).type("{enter}")
  page.Chat.Message(message).should("exist")
})

const message = `Hello, World! ${v1()}`
const email = `${v1()}zdu.strong@gmail.com`
const password = '葫芦娃, 葫芦娃, 一根藤上七个娃'
