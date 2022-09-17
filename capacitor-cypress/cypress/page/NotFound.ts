export default {
  NotFoundText: () => cy.xpath(`//*[text()='Not Found']`),
  ReturnToHomeButton: () => cy.xpath(`//button[text()='Back home']`),
}