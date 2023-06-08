export default {
  signOutButton: () => cy.xpath("//Button[.='Sign out']", { timeout: 60000 }),
  MessageContentInput: () => cy.xpath(`//*[text()='Message content']/..//textarea[@rows]`),
  Message: (message: string) => cy.xpath(`//*[contains(text(), '${message}')]`),
  RecallMessageButton: (message: string) => cy.xpath(`//*[contains(text(), '${message}')]/..//button[text()='Withdrawn']`),
}