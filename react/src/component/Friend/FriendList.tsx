import { FriendshipModel } from "@/model/FriendshipModel";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { useMount } from "react-use";
import FriendChildComponent from "./FriendChildComponent";

export default observer(() => {
  const state = useMobxState({
    friendshipList: [] as FriendshipModel[],
  }, {

  });

  useMount(async () => {
  })

  return <div className="flex flex-col">
    {state.friendshipList.map(item => <FriendChildComponent
      friend={item}
    />)}
  </div>;
})