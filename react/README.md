# Getting Started

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app). If you have any questions, please contact zdu.strong@gmail.com.<br/>

## Development environment setup
1. From https://code.visualstudio.com install Visual Studio Code.<br/>
2. From https://nodejs.org install nodejs v18.<br/>

## Available Scripts

In the project directory, you can run:<br/>

### `npm start`

Runs the app in the development mode.<br/>
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.<br/>

The page will reload if you make edits.<br/>
You will also see any lint errors in the console.<br/>

### `npm run build`

Builds the app for production to the `build` folder.<br/>
It correctly bundles React in production mode and optimizes the build for the best performance.<br/>

The build is minified and the filenames include the hashes.<br/>
Your app is ready to be deployed!<br/>

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.<br/>

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

1. React UI framework (https://reactjs.org)<br/>
2. mobx-react-use-autorun (https://www.npmjs.com/package/mobx-react-use-autorun)
3. typestyle (https://www.npmjs.com/package/typestyle)<br/>
4. Material UI Components (https://material-ui.com)<br/>
5. Material Icons (https://mui.com/material-ui/material-icons)<br/>
6. Loading icon (https://mui.com/material-ui/react-progress)<br/>
7. axios (https://axios-http.com/)
8. linq (https://www.npmjs.com/package/linq)<br/>
9. date-fns (https://date-fns.org)
10. UUID (https://www.npmjs.com/package/uuid)<br/>
11. Recharts (https://recharts.org/en-US)<br/>
12. tailwindcss(https://tailwindcss.com)<br/>
13. Animate.css(https://animate.style)<br/>
14. react-git-info (https://www.npmjs.com/package/react-git-info)
