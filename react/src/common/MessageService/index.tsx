import { v1 } from 'uuid';
import { observable } from 'mobx-react-use-autorun'
import { FormattedMessage } from 'react-intl';
import { GlobalExactMessageMatch } from '@/common/MessageService/GlobalExactMessageMatch';

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

export const GlobalMessageList = observable([]) as { id: string, message: string, type: string }[];

export function getI18nMessageReactNode(message: string) {
  let messageOfI18n = getMessageString(message);
  if (GlobalExactMessageMatch[message]) {
    return <FormattedMessage id={GlobalExactMessageMatch[message]} defaultMessage={message} />
  }
  return messageOfI18n;
}

export const MESSAGE_TYPE_ENUM = {
  error: "error",
  warning: "warning",
  info: "info",
  success: "success"
}

function handleMessage(type: string, message: string | string[]) {
  GlobalMessageList.splice(0, GlobalMessageList.length);
  if (message instanceof Array) {
    for (const messageItem of message) {
      GlobalMessageList.push(getMessageObject(type, messageItem))
    }
  } else {
    GlobalMessageList.push(getMessageObject(type, message))
  }
}

function getMessageObject(type: string, message: any) {
  return {
    id: v1(),
    message: getMessageString(message),
    type: type
  }
}

function getMessageString(message: any): string {
  if (typeof message === "object" && message instanceof Array) {
    return getMessageString(message![0]);
  }

  let messageContent = "";
  if (typeof message === "string") {
    messageContent = message;
  } else if (typeof message === "number") {
    messageContent = String(message);
  } else if (typeof message!.message === "string") {
    messageContent = message.message;
  } else if (typeof message!.error === "string") {
    messageContent = message.error;
  }
  return messageContent;
}
