import { v1 } from 'uuid'
import page from '../../page'

it('', () => {
  page.SignUp.nickname().type('John Hancock')
  page.SignUp.nextStepButton().click()
  page.SignUp.password().type('Hello, World!')
  page.SignUp.nextStepButton().click()
  page.SignUp.addEmailOrPhoneNumber().click()
  page.SignUp.email().type(email)
  page.SignUp.sendVerificationCodeButton().click()
  page.SignUp.verificationCodeInput().type('123456')
  page.SignUp.nextStepButton().click()
  page.SignUp.signUpButton().click()
  cy.location('pathname', { timeout: 180000 }).should('equal', "/chat")
})

before(() => {
  cy.visit("/sign_up")
})

const email = `${v1()}zdu.strong@gmail.com`