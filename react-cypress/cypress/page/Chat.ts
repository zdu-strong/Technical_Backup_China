export default {
  signOutButton: () => cy.xpath("//Button[.='Sign out']"),
  MessageContentInput: () => cy.xpath(`//*[text()='Message content']/..//textarea[@rows]`),
  Message: (message: string) => cy.xpath(`//*[contains(text(), '${message}')]`),
  RecallMessageButton: (message: string) => cy.xpath(`//*[contains(text(), '${message}')]/..//button[text()='Withdrawn']`),
}