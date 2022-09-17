import { BottomNavigation, BottomNavigationAction } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import RestoreIcon from '@mui/icons-material/Restore';
import FavoriteIcon from '@mui/icons-material/Favorite';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import FriendList from "../Friend/FriendList";
import StrangerList from "../Stranger/StrangerList";
import { NavigationEnum } from "./js/NavigationEnum";
import { useNavigate, useSearchParams } from "react-router-dom";
import { FormattedMessage } from "react-intl";

export default observer(() => {

  const state = useMobxState({
    navigation: NavigationEnum.Friend,
  }, {
    navigate: useNavigate(),
    ...((() => {
      var [URLSearchParams, SetURLSearchParams] = useSearchParams();
      return {
        URLSearchParams,
        SetURLSearchParams
      };
    })()),
  })

  return <>
    <div className="flex flex-col flex-auto">
      <div className="flex flex-row flex-auto">
        {state.navigation === NavigationEnum.Friend && <FriendList />}
        {state.navigation === NavigationEnum.Stranger && <StrangerList />}
        {state.navigation === NavigationEnum.Setting && <div><FormattedMessage id="Setting" defaultMessage="Setting" /></div>}
      </div>
      <div className="flex flex-row">
        <BottomNavigation
          showLabels
          value={state.navigation}
          onChange={(event, newValue) => {
            state.navigation = newValue;
          }}
        >
          <BottomNavigationAction value={NavigationEnum.Friend} label="好友" icon={<RestoreIcon />} />
          <BottomNavigationAction value={NavigationEnum.Stranger} label="陌生人" icon={<FavoriteIcon />} />
          <BottomNavigationAction value={NavigationEnum.Setting} label="设置" icon={<LocationOnIcon />} />
        </BottomNavigation>
      </div>
    </div>
  </>;
})