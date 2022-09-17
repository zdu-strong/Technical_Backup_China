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
npx npm-check --update<br/>

After upgrading the dependencies, please make sure that the project runs normally.<br/>
After upgrading the dependencies, please make sure that the dependent versions in package.json are all accurate versions.<br/>

The following dependencies are currently unable to continue to be upgraded:<br/>
execa (Current project not support ES module)<br/>
get-port (Current project not support ES module)<br/>
linq (Current project not support ES module)<br/>

## Notes - CSS In JS with style

Define componentized css, & represents the current class name.

    import { stylesheet } from 'typestyle';

    export default () => {
        return <div className={css.divStyle}></div>
    }

    const css = stylesheet({
        divStyle: {
            color: "green",
            $nest: {
                "&:hover": {
                    color: "yellow",
                },
                "& .modal .modal-body": {
                    color: "red",
                },
                "table tbody th&.blue-color:first-child": {
                    color: 'blue',
                }
            }
        }
    })}

## Notes - Define state and props with useMobxState

    import { useMobxState, observer } from 'mobx-react-use-autorun';

    export default observer((props: {name: string}) => {

        const state = useMobxState({ randomNumber: 1 }, {...props});

        return <div onClick={() => state.randomNumber = Math.random()}>
            {state.randomNumber}
        </div>
    })

more usage:<br/>
Form validation<br/>

    import { Button, TextField } from '@mui/material';
    import { observer, useMobxState } from 'mobx-react-use-autorun';
    import { MessageService } from '@/common/MessageService';

    export default observer(() => {

        const state = useMobxState(() => ({
            name: "",
            submit: false,
            errors: {
                name() {
                    return state.submit && !state.name && "请填写名称";
                },
                hasError() {
                    return Object.keys(state.errors).filter(s => s !== "hasError").some(s => (state.errors as any)[s]());
                }
            }
        }));

        const ok = async () => {
            state.submit = true;
            if (state.errors.hasError()) {
                MessageService.error("错误");
            } else {
                MessageService.success("提交成功");
            }
        }

        return (<div className='flex flex-col' style={{ padding: "2em" }}>
            <TextField value={state.name} label="用户名" onChange={(e) => state.name = e.target.value} error={!!state.errors.name()} helperText={state.errors.name()} />
            <Button variant="contained" style={{ marginTop: "2em" }} onClick={ok} >提交</Button>
        </div>)
    })

## Notes - Subscription property changes with useMobxEffect

    import { useMobxState, observer, useMobxEffect, toJS } from 'mobx-react-use-autorun';

    export default observer(() => {

        const state = useMobxState({ randomNumber: 1 });

        useMobxEffect(() => {
            console.log(toJS(state))
        }, [state]);

        return <div onClick={() => state.randomNumber = Math.random()}>
            {state.randomNumber}
        </div>
    })

## Notes - Define global mutable data

    import { observable } from 'mobx-react-use-autorun';

    const state = observable({});

## Notes - Get the real data of the proxy object with toJS

    import { observable, toJS } from 'mobx-react-use-autorun';

    const state = observable({});
    console.log(toJS(state));

## Notes - Do something when the component mount with useMount

    import { useMount } from 'react-use';

    useMount(async () => {
        console.log("mount")
    })

## Notes - Do something when the component unmount with useUnmount

    import { useUnmount } from 'react-use';
    import { Subscription } from 'rxjs';

    const state = useMobxState({
        subscription: new Subscription()
    });

    useUnmount(async () => {
        state.subscription.unsubscribe()
    })

## Learn More

1. React UI framework (https://reactjs.org)<br/>
2. Material UI Components (https://material-ui.com)<br/>
3. Material Icons (https://mui.com/material-ui/material-icons)<br/>
4. Loading icon (https://mui.com/material-ui/react-progress)<br/>
5. tailwindcss(https://tailwindcss.com)<br/>
6. Animate.css(https://animate.style)<br/>
7. UUID (https://www.npmjs.com/package/uuid)<br/>
8. typestyle (https://www.npmjs.com/package/typestyle)<br/>
9. linq (https://www.npmjs.com/package/linq)<br/>
10. Electron (https://www.electronjs.org)<br/>