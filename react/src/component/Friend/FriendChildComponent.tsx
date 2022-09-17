import { FriendshipModel } from "@/model/FriendshipModel";
import { observer, useMobxState } from "mobx-react-use-autorun";

export default observer((props: {friend: FriendshipModel})=>{

  const state = useMobxState({

  }, {
    ...props,
  });

  return <div className="flex flex-row">
    {state.friend.friend?.username}
  </div>;
})