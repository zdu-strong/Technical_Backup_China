import { observer, useMobxState } from "mobx-react-use-autorun";
import { MessageService } from "@/common/MessageService";
import api from '@/api'
import { AppBar, Box, Button, CircularProgress, Toolbar, Typography } from "@mui/material";
import { isMobilePhone } from "@/common/is-mobile-phone";
import { useNavigate } from "react-router-dom";
import LogoutIcon from '@mui/icons-material/Logout';
import { FormattedMessage } from "react-intl";

export default observer((props: { username: string, userId: string }) => {

  const state = useMobxState({
    loadingOfSignOut: false,
  }, {
    ...props,
    navigate: useNavigate(),
  })

  async function signOut() {
    if (state.loadingOfSignOut) {
      return;
    }
    try {
      state.loadingOfSignOut = true;
      await api.Authorization.signOut();
      state.navigate("/sign_in");
    } catch (e) {
      MessageService.error(e);
      state.loadingOfSignOut = false;
    }
  }

  return <Box style={{ marginBottom: "1em", width: "100%" }}>
    <AppBar position="static">
      <Toolbar className="flex flex-row justify-end" style={{ paddingLeft: "0px" }}>
        <div className="flex flex-row items-center">
          <Typography variant="h6" component="div" sx={{ flexWrap: "nowrap" }}>
            <div style={{ marginLeft: "1em", ...(isMobilePhone ? { fontSize: "x-small" } : {}) }}>
              {state.username}
            </div>
          </Typography>
          <Button
            variant="contained"
            color="secondary"
            startIcon={state.loadingOfSignOut ? <CircularProgress style={{ color: "white" }} size="22px" /> : <LogoutIcon />}
            onClick={signOut}
            style={{
              marginLeft: "1em",
              textTransform: "none"
            }}
          >
            <FormattedMessage id="SignOut" defaultMessage="Sign out" />
          </Button>
        </div>
      </Toolbar>
    </AppBar>
  </Box>
})