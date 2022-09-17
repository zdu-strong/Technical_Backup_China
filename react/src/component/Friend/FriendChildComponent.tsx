import { FriendshipModel } from "@/model/FriendshipModel";
import { Button, CircularProgress } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import api from "@/api";
import { MessageService } from "@/common/MessageService";
import DeleteIcon from '@mui/icons-material/Delete';
import { FormattedMessage } from "react-intl";

export default observer((props: { friendship: FriendshipModel, refreshFriendList: () => Promise<void> }) => {

  const state = useMobxState({
    loading: false,
  }, {
    ...props,
  });


  async function deleteFromFriendList() {
    if (state.loading) {
      return;
    }
    try {
      state.loading = true;
      await api.Friendship.deleteFromFriendList(state.friendship.friend?.id!)
      await state.refreshFriendList();
    } catch (error) {
      MessageService.error(error)
    } finally {
      state.loading = false
    }
  }

  return <div className="flex flex-row justify-between">
    <div>
      {state.friendship.friend?.username}
    </div>
    <Button
      variant="contained"
      style={{
        marginRight: "1em",
        textTransform: "none"
      }}
      startIcon={
        state.loading ? <CircularProgress /> : <DeleteIcon />
      }
      onClick={deleteFromFriendList}
    >
      <FormattedMessage id="LiftYourFriends" defaultMessage="Lift your friends" />
    </Button>
  </div>;
})