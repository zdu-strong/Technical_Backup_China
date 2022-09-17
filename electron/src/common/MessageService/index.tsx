import { v1 } from 'uuid';
import { observable } from 'mobx-react-use-autorun'

export const MessageService = {
  error: (message: string | string[] & any) => {
    handleMessage(MESSAGE_TYPE_ENUM.error, message)
  },
  warning: (message: string | string[] | any) => {
    handleMessage(MESSAGE_TYPE_ENUM.warning, message)
  },
  info: (message: string | string[] & any) => {
    handleMessage(MESSAGE_TYPE_ENUM.info, message)
  },
  success: (message: string | string[] & any) => {
    handleMessage(MESSAGE_TYPE_ENUM.success, message)
  }
}

const handleMessage = (type: string, message: string | string[]) => {
  GlobalMessageList.splice(0, GlobalMessageList.length);
  if (message instanceof Array) {
    for (const messageItem of message) {
      GlobalMessageList.push(getMessage(type, messageItem))
    }
  } else {
    GlobalMessageList.push(getMessage(type, message))
  }
}

export const getMessage = (type: string, message: any) => {
  let messageContent = String(message);
  if (!message && typeof message !== "number") {
    messageContent = "";
  }
  if (typeof message?.message === "string") {
    messageContent = message.message;
  }
  if (!message?.message && message?.error && typeof message?.error === "string") {
    messageContent = message.error;
  }
  return {
    id: v1(),
    message: messageContent,
    type: type
  }
}

export const GlobalMessageList = observable([]) as { id: string, message: string, type: string }[];

export const MESSAGE_TYPE_ENUM = {
  error: "error",
  warning: "warning",
  info: "info",
  success: "success"
}