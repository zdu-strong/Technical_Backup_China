export default {
  CurrentPower: () => cy.xpath(`//*[contains(text(),'current battery')]`),
  EnterGame: () => cy.xpath(`//button[contains(., 'Enter the game')]`),
  Game: () => cy.xpath(`//canvas`),
  Setting: () => cy.xpath(`//*[@data-testid='SettingsIcon']/..`),
  EndTheGameButton: () => cy.xpath(`//button[contains(., 'End Game')]`),
}