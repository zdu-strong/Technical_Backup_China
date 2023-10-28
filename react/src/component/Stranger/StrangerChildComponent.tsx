import { FriendshipModel } from "@/model/FriendshipModel";
import { Button } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { MessageService } from "@/common/MessageService";
import api from "@/api";
import { FormattedMessage } from "react-intl";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faSpinner } from "@fortawesome/free-solid-svg-icons";

export default observer((props: { friendship: FriendshipModel, refreshFriendshipList: () => Promise<void> }) => {

  const state = useMobxState({
    loading: false
  }, {
    ...props,
  });

  async function addFriend() {
    if (state.loading) {
      return;
    }
    try {
      state.loading = true;
      if (!state.friendship.aesOfUser) {
        await api.Friendship.createFriendship(state.friendship.friend?.id!);
      }
      await api.Friendship.addFriend(state.friendship.friend?.id!);
      await state.refreshFriendshipList();
    } catch (error) {
      MessageService.error(error);
    } finally {
      state.loading = false;
    }
  }

  return <div className="flex flex-row justify-between items-center">
    <div className="flex flex-row">
      {state.friendship.friend?.username}
    </div>
    <Button
      variant="contained"
      style={{
        marginRight: "1em",
        textTransform: "none"
      }}
      startIcon={<FontAwesomeIcon icon={state.loading ? faSpinner : faPlus} spin={state.loading} />}
      onClick={addFriend}
    >
      <FormattedMessage id="AddToFriends" defaultMessage="Add to friends" />
    </Button>
  </div>;
})