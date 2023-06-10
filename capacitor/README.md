# Getting Started

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app). If you have any questions, please contact zdu.strong@gmail.com.<br/>

## Development environment setup
1. From https://code.visualstudio.com install Visual Studio Code.<br/>
2. From https://nodejs.org install nodejs v18.<br/>
3. For Android:<br/>
From https://adoptium.net install java v17, and choose Entire feature.<br/>
4. For Android:<br/>
From https://developer.android.com/studio install Android Studio. Next, create virtual device (Phone - Pixel 6 - Android 13.0) and launch it.<br/>
5. For IOS:<br/>
Follow this document (https://capacitorjs.com/docs/getting-started/environment-setup) to install Xcode.<br/>

## Available Scripts

In the project directory, you can run:<br/>

### `npm start`

Runs the app in the development mode.<br/>

The app will reload if you make edits.<br/>
You will also see any lint errors in the console.<br/>

Open chrome://inspect in chrome to use development tools.<br/>

### `npm run pack`

It builds and deploys the native app to a target device of your choice.<br/>

### `npm run make`

This command will build the native project to create a signed AAB, APK or IPA file. Build options can be specified on the command line or in your Capacitor Configuration File.

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
linq (Current project not support ES module)<br/>
get-port (Current project not support ES module)<br/>
inquirer (Current project not support ES module)<br/>
typescript (Dependency incompatibility)<br/>

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

## Learn More

1. React UI framework (https://react.dev)<br/>
2. mobx-react-use-autorun (https://www.npmjs.com/package/mobx-react-use-autorun)
3. typestyle (https://www.npmjs.com/package/typestyle)<br/>
4. Material UI Components (https://material-ui.com)<br/>
5. Material Icons (https://mui.com/material-ui/material-icons)<br/>
6. Loading icon (https://mui.com/material-ui/react-progress)<br/>
7. Capacitor Plugins (https://capacitorjs.com/docs/apis)<br/>
8. axios (https://axios-http.com)
9. typedjson (https://www.npmjs.com/package/typedjson)
10. linq (https://www.npmjs.com/package/linq)<br/>
11. UUID (https://www.npmjs.com/package/uuid)<br/>
12. tailwindcss (https://tailwindcss.com)<br/>
13. Animate.css (https://animate.style)<br/>
14. react-git-info (https://www.npmjs.com/package/react-git-info)
15. Game Engine (https://www.babylonjs.com)
16. mathjs (https://www.npmjs.com/package/mathjs)
