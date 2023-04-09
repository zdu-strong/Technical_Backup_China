# Getting Started

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app). If you have any questions, please contact zdu.strong@gmail.com.<br/>

## Development environment setup
1. From https://code.visualstudio.com install Visual Studio Code.<br/>
2. From https://nodejs.org install nodejs v18.<br/>

## Available Scripts

In the project directory, you can run:<br/>

### `npm start`

Runs the app in the development mode.

The app will reload if you make edits.<br/>
You will also see any lint errors in the console.

### `npm run pack`

only generates the package directory without really packaging it. This is useful for testing purposes.

### `npm run make`

to package in a distributable format (e.g. dmg, windows installer, deb package).

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
linq (Current project not support ES module)<br/>
typescript (Dependency incompatibility)<br/>

## Notes - Things to note

1. The dependencies used in the Electron main process need to be installed in dependencies, and other dependencies are installed in devDependencies. This can keep the application size to a minimum.

## Notes - CSS In JS with style

Define componentized css, & represents the current class name.

    import { stylesheet } from 'typestyle';
    import { useMobxState, observer } from 'mobx-react-use-autorun';

    export default observer(() => {

      const state = useMobxState({
        css: stylesheet({
          container: {
            color: "green",
            $nest: {
              "&:hover": {
                color: "yellow",
              },
            }
          }
        })
      })

      return <div className={state.css.container}></div>
    })

## Notes - typedjson

    import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
    import { makeAutoObservable } from 'mobx-react-use-autorun'

    @jsonObject
    export class UserModel {

      @jsonMember(String)
      username!: string;

      @jsonArrayMember(UserModel)
      childList!: UserModel[];

      constructor() {
        makeAutoObservable(this);
      }
    }

## Learn More

1. React UI framework (https://reactjs.org)<br/>
2. mobx-react-use-autorun (https://www.npmjs.com/package/mobx-react-use-autorun)
3. typestyle (https://www.npmjs.com/package/typestyle)<br/>
4. Material UI Components (https://material-ui.com)<br/>
5. Material Icons (https://mui.com/material-ui/material-icons)<br/>
6. Loading icon (https://mui.com/material-ui/react-progress)<br/>
7. Electron (https://www.electronjs.org)<br/>
8. axios (https://axios-http.com)
9. typedjson (https://www.npmjs.com/package/typedjson)
10. linq (https://www.npmjs.com/package/linq)<br/>
11. UUID (https://www.npmjs.com/package/uuid)<br/>
12. tailwindcss(https://tailwindcss.com)<br/>
13. Animate.css(https://animate.style)<br/>
14. react-git-info (https://www.npmjs.com/package/react-git-info)
15. Game Engine (https://www.babylonjs.com)
16. mathjs (https://www.npmjs.com/package/mathjs)
