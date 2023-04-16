import * as UtilType from '@/../main/util'
import * as RemoteType from '@electron/remote';
import * as NodeOsUtilsType from 'node-os-utils';
import * as FSType from 'fs';
import * as StorageManageRunUtilType from '@/../main/util/storage_manage/StorageManageRun'

const remote = window.require("@electron/remote") as typeof RemoteType;

export default {
  ...remote,
  NodeOsUtils: remote.require("node-os-utils") as typeof NodeOsUtilsType,
  fs: remote.require('fs') as typeof FSType,
  ...(remote.require("./util") as typeof UtilType),
  StorageManageRunUtil: (remote.require("./util/storage_manage/StorageManageRun") as typeof StorageManageRunUtilType).default,
}