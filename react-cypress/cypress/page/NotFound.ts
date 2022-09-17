export default {
    NotFoundText: () => cy.xpath(`//*[text()='404']`),
    返回主页按钮: () => cy.xpath(`//button[text()='To home']`),
}