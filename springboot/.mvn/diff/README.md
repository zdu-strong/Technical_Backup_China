# Getting Started

This project was bootstrapped with [NestJS](https://nestjs.com). If you have any questions, please contact zdu.strong@gmail.com.<br/>

## Development environment setup
1. From https://code.visualstudio.com install Visual Studio Code.<br/>
2. From https://nodejs.org install nodejs v16.<br/>

## Available Scripts

In the project directory, you can run:<br/>

### `npm start`

Produce executable files that can be run directly on the machine (windows, mac, linux).<br/>
For example: diff.exe, diff-macos, diff-linux<br/>
It can be called with ./diff in the springboot folder in the root directory of the repository.<br/>

## Install new dependencies

    npm install react --save-dev

After installing new dependencies, please make sure that the project runs normally.<br/>
After installing new dependencies, please make sure that the dependent versions in package.json are all accurate versions.<br/>

## Upgrade dependency

You can use this command to check if a new version is available:<br/>
npx npm-check --update<br/>

After upgrading the dependencies, please make sure that the project runs normally.<br/>
After upgrading the dependencies, please make sure that the dependent versions in package.json are all accurate versions.<br/>

The following dependencies are currently unable to continue to be upgraded:<br/>
execa (Current project not support ES module)<br/>
get-port (Current project not support ES module)<br/>
