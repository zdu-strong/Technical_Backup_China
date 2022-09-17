import { v1 } from 'uuid'
import page from '../../page'
import * as action from '../../action'

it('', () => {
  page.SignIn.username().clear().type(email)
  page.SignIn.showPasswordButton().click()
  page.SignIn.password().clear().type(password)
  page.SignIn.signInButton().click()
  cy.location('pathname', { timeout: 60000 }).should('equal', '/chat')
})

before(() => {
  cy.visit("/sign_up")
  action.signUp(email, password)
  page.Chat.signOutButton().click()
  cy.location('pathname').should('equal', '/sign_in')
})

const email = `${v1()}zdu.strong@gmail.com`
const password = '葫芦娃, 葫芦娃, 一根藤上七个娃'