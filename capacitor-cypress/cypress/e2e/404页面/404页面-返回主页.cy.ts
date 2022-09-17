import page from '../../page'
import * as action from '../../action'

it('', () => {
    page.NotFound.返回主页按钮().click()
    page.HomePage.当前电量().should("exist")
})

before(() => {
    action.setPhoneViewport()
    cy.visit("/404")
})