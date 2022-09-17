import page from '../../page'
import * as action from '../../action'

it('', () => {
    page.NotFound.NotFoundText().should("exist")
})

before(() => {
    action.setPhoneViewport()
    cy.visit("/any_page")
})