import page from '../../page'
import * as action from '../../action'

it('', () => {
    page.HomePage.当前电量().should("exist")
})

before(() => {
    action.setPhoneViewport()
    cy.visit("/")
})