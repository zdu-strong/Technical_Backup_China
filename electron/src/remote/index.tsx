import * as UtilType from '@/../main/util'
import * as RemoteType from '@electron/remote';
import * as NodeOsUtilsType from 'node-os-utils';
import * as FSType from 'fs';

const remote = window.require("@electron/remote") as typeof RemoteType;

export default {
  ...remote,
  NodeOsUtils: window.require("node-os-utils") as typeof NodeOsUtilsType,
  fs: window.require('fs') as typeof FSType,
  ...(remote.require("./util") as typeof UtilType),
}