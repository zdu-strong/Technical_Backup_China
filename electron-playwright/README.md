# Getting Started

This project was bootstrapped with [NestJS](https://nestjs.com). If you have any questions, please contact zdu.strong@gmail.com.<br/>

## Development environment setup
1. From https://code.visualstudio.com install Visual Studio Code.<br/>
2. From https://nodejs.org install nodejs v18.<br/>

## Available Scripts

In the project directory, you can run:<br/>

### `npm start`

Launches the test runner in the interactive watch mode.<br/>
See the section about [running tests](https://playwright.dev) for more information.<br/>

### `npm test`

Run all unit tests.<br/>
See the section about [running tests](https://playwright.dev) for more information.<br/>

## Install new dependencies

    npm install react --save-dev

After installing new dependencies, please make sure that the project runs normally.<br/>
After installing new dependencies, please make sure that the dependent versions in package.json are all accurate versions.<br/>

## Upgrade dependency

You can use this command to check if a new version is available:<br/>

    npx npm-check --update

After upgrading the dependencies, please make sure that the project runs normally.<br/>
After upgrading the dependencies, please make sure that the dependent versions in package.json are all accurate versions.<br/>

The following dependencies are currently unable to continue to be upgraded:<br/>
execa (Current project not support ES module)<br/>
get-port (Current project not support ES module)<br/>

## Notes - xpath - Select the node whose text contains "abc"

    html:
        <div>
          <div class="flex">
            abcd
          </div>
          <div class="flex">
            hijk
          </div>
        </div>
    xpath: //div[contains(@class, 'flex')][contains(., 'abc')]
    count: 1

## Learn More

1. Playwright (https://playwright.dev)<br/>
2. Xpath (https://www.w3schools.com/xml/xpath_syntax.asp)<br/>
