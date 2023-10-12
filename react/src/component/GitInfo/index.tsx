import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { TextField } from "@mui/material";

const css = stylesheet({
  container: {
    width: "20em",
    $nest: {
      "& > div:first-child": {
        paddingTop: "3em",
        paddingLeft: "1em",
        paddingRight: "1em",
        paddingBottom: "1em",
        display: "flex",
        justifyContent: "right"
      },
      "& > div:last-child": {
        paddingLeft: "1em",
        paddingRight: "1em",
        textAlign: "right"
      }
    }
  }
});

export default observer(() => {

  const state = useMobxState({
    name: '',
  })

  return <div className={css.container}>
    <div>
      <TextField
        label={"Your Name"}
        variant="outlined"
        onChange={(e) => {
          state.name = e.target.value;
        }}
        value={state.name}
        autoComplete="off"
        autoFocus={true}
      />
    </div>
    <div>
      {`- Welcome back, ${state.name}!`}
    </div>
  </div>
})