let ServerAddress = 'http://127.0.0.1:8080';
let ClientAddress = 'http://127.0.0.1:3000';

const existWindow = (() => {
  try {
    if (window) {
      return true;
    } else {
      return false;
    }
  } catch {
    return false;
  }
})();

if (existWindow) {
  ServerAddress = window.location.protocol + "//" + window.location.hostname + ":" + 8080;

  if (process.env.NODE_ENV === "development") {
    if (process.env.REACT_APP_SERVER_PORT) {
      ServerAddress = window.location.protocol + "//" + window.location.hostname + ":" + process.env.REACT_APP_SERVER_PORT;
    }
  }
}

if (existWindow) {
  ClientAddress = window.location.origin;
}

const serverUrl = new URL(ServerAddress);

export { ClientAddress };
export { ServerAddress };
export const WebSocketServerAddress = `${serverUrl.protocol.replace("http", "ws")}//${serverUrl.host}`;
