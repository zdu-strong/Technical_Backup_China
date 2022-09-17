export default {
  CurrentPower: () => cy.xpath(`//*[contains(text(),'current battery')]`),
}