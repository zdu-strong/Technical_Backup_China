import * as Remote from '@electron/remote';
import * as NodeOsUtils from 'node-os-utils';
import * as fs from 'fs';
import * as StorageManageRunUtil from '@/../main/util/storage_manage/StorageManageRun'
import * as GetBuildFolderPathUtil from '@/../main/util/GetBuildFolderPathUtil'
import * as GetPublicFolderPathUtil from '@/../main/util/GetPublicFolderPathUtil'
import * as IsLoadedUtil from '@/../main/util/IsLoadedUtil'
import * as IsNotShowForTestUtil from '@/../main/util/IsNotShowForTestUtil'
import * as IsPackagedUtil from '@/../main/util/IsPackagedUtil';
import * as LoadWindowFromRelativeUrlUtil from '@/../main/util/LoadWindowFromRelativeUrlUtil'
import * as ElectronStorage from '@/../main/util/StorageUtil'
import * as ElectronDatabase from '@/../main/util/ElectronDatabaseInstanceUtil'

const remote = window.require("@electron/remote") as typeof Remote;

export default {
  ...remote,
  NodeOsUtils: remote.require("node-os-utils") as typeof NodeOsUtils,
  fs: remote.require('fs') as typeof fs,
  StorageManageRunUtil: (remote.require("./util/storage_manage/StorageManageRun") as typeof StorageManageRunUtil).default,
  ...(remote.require("./util/GetBuildFolderPathUtil") as typeof GetBuildFolderPathUtil),
  ...(remote.require("./util/GetPublicFolderPathUtil") as typeof GetPublicFolderPathUtil),
  ...(remote.require("./util/IsLoadedUtil") as typeof IsLoadedUtil),
  ...(remote.require("./util/IsNotShowForTestUtil") as typeof IsNotShowForTestUtil),
  ...(remote.require("./util/IsPackagedUtil") as typeof IsPackagedUtil),
  ...(remote.require("./util/LoadWindowFromRelativeUrlUtil") as typeof LoadWindowFromRelativeUrlUtil),
  ElectronStorage: remote.require("./util/StorageUtil") as typeof ElectronStorage,
  ElectronDatabase: remote.require("./util/ElectronDatabaseInstanceUtil") as typeof ElectronDatabase,
}