export default {
  NotFoundText: () => cy.xpath(`//*[text()='404']`),
  ReturnToHomeButton: () => cy.xpath(`//button[text()='To home']`),
}