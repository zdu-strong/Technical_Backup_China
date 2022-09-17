export default {
    NotFoundText: () => cy.xpath(`//*[text()='Not Found']`),
    返回主页按钮: () => cy.xpath(`//button[text()='Back home']`),
}