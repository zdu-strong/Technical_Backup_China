export default {
    用户邮箱输入框: () => cy.xpath(`//*[text()='Please enter your email']/..//input`),
    开始使用按钮: () => cy.xpath("//*[text()='Start using']"),
    消息内容输入框: () => cy.xpath(`//*[text()='Message content']/..//textarea[@rows]`),
    消息: (消息: string) => cy.xpath(`//*[contains(text(), '${消息}')]`),
    撤回按钮: (消息: string) => cy.xpath(`//*[contains(text(), '${消息}')]/..//button[text()='Withdrawn']`),

}