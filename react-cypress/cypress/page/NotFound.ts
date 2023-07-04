export default {
  NotFoundText: () => cy.xpath(`//div[contains(@class, 'MuiPaper')]/div[contains(., '404')]`),
  ReturnToHomeButton: () => cy.xpath(`//button[contains(., 'To home')]`),
}