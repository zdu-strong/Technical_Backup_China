import page from '../../page'
import { v1 } from 'uuid'

it('', () => {
    page.HomePage.撤回按钮(消息).click()
    page.HomePage.消息(消息).should("not.exist")
})

before(() => {
    cy.visit("/")
    page.HomePage.用户邮箱输入框().type("zdu.strong@gmail.com").type("{enter}")
    page.HomePage.消息内容输入框().type(消息).type("{enter}")
    page.HomePage.消息(消息).should("exist")
})

const 消息 = `Hello, World! ${v1()}`