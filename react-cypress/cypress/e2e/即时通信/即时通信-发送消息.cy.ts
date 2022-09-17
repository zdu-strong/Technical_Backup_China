import page from '../../page'
import { v1 } from 'uuid'

it('', () => {
    page.HomePage.用户邮箱输入框().type("zdu.strong@gmail.com")
    page.HomePage.开始使用按钮().click()
    page.HomePage.消息内容输入框().type(消息).type("{enter}")
    page.HomePage.消息(消息).should("exist")
})

before(() => {
    cy.visit("/")
})

const 消息 = `Hello, World! ${v1()}`