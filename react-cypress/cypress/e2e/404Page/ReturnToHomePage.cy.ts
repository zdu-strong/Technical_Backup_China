import page from '../../page'

it('', () => {
  page.NotFound.ReturnToHomeButton().click()
  page.SignIn.signInButton().should('exist')
})

before(() => {
  cy.visit("/404")
})