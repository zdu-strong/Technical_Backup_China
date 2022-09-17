import execa from 'execa';
import path from 'path';
import { v1 } from 'uuid';
import getPort from 'get-port';
import waitOn from 'wait-on';
import treeKill from 'tree-kill';
import util from 'util';
import { formatInTimeZone } from 'date-fns-tz';
import fs from 'fs';

async function main() {
  const newDatabaseName = v1().replaceAll('-', '_');
  const oldDatabaseName = v1().replaceAll('-', '_');
  await buildNewDatabase(newDatabaseName);
  const isCreateChangeLogFile = await diffDatabase(
    newDatabaseName,
    oldDatabaseName,
  );
  await deleteDatabase(newDatabaseName, oldDatabaseName);
  if (!isCreateChangeLogFile) {
    console.log('\nAn empty changelog file was generated, so delete it.');
  }
}

async function buildNewDatabase(newDatabaseName: string) {
  const availableServerPort = await getUnusedPort();
  const command = `./mvn clean compile spring-boot:run --define database.mysql.name=${newDatabaseName}`;
  const childProcess = execa.command(command, {
    stdio: 'inherit',
    cwd: getBaseFolderPath(),
    extendEnv: true,
    env: {
      SERVER_PORT: `${availableServerPort}`,
      SPRING_JPA_HIBERNATE_DDL_AUTO: 'update',
      SPRING_LIQUIBASE_ENABLED: 'false',
      PROPERTIES_STORAGE_ROOT_PATH: 'classpath:diff-for-new-database',
    },
  });
  await Promise.race([
    childProcess,
    waitOn({
      resources: [`http://127.0.0.1:${availableServerPort}`],
    }),
  ]);
  await util.promisify(treeKill)(childProcess.pid);
}

async function diffDatabase(
  newDatabaseName: string,
  oldDatabaseName: string,
): Promise<boolean> {
  const today = new Date();
  const filePathOfDiffChangeLogFile = path
    .join(
      getBaseFolderPath(),
      'src/main/resources',
      'liquibase/changelog',
      formatInTimeZone(today, "UTC", 'yyyy.MM.dd'),
      `${formatInTimeZone(today, "UTC", 'yyyy.MM.dd.HH.mm.ss')}_changelog.xml`,
    )
    .replaceAll('\\', '/');
  const isCreateFolder = !(await existFolder(
    path.join(filePathOfDiffChangeLogFile, '..'),
  ));
  if (isCreateFolder) {
    await fs.promises.mkdir(path.join(filePathOfDiffChangeLogFile, '..'), {
      recursive: true,
    });
  }
  const command = `./mvn clean compile liquibase:update liquibase:diff --define database.mysql.name=${oldDatabaseName}`;
  await execa.command(command, {
    stdio: 'inherit',
    cwd: getBaseFolderPath(),
    extendEnv: true,
    env: {
      LIQUIBASE_DIFF_CHANGELOG_FILE: filePathOfDiffChangeLogFile,
      PROPERTIES_STORAGE_ROOT_PATH: 'classpath:diff-for-old-database',
      LIQUIBASE_REFERENCE_DATABASE_NAME: newDatabaseName,
    },
  });
  const textContentOfDiffChangeLogFile = await fs.promises.readFile(
    filePathOfDiffChangeLogFile,
    { encoding: 'utf8' },
  );
  const isEmptyOfDiffChangeLogFile =
    !textContentOfDiffChangeLogFile.includes('</changeSet>');
  if (isEmptyOfDiffChangeLogFile) {
    if (isCreateFolder) {
      await fs.promises.rm(path.join(filePathOfDiffChangeLogFile, '..'), {
        recursive: true,
        force: true,
      });
    } else {
      await fs.promises.rm(filePathOfDiffChangeLogFile, {
        recursive: true,
        force: true,
      });
    }

    const filePathOfDerbyLog = path.join(getBaseFolderPath(), 'derby.log');
    await fs.promises.rm(filePathOfDerbyLog, {
      recursive: true,
      force: true,
    });
    return false;
  }
  return true;
}

async function deleteDatabase(
  newDatabaseName: string,
  oldDatabaseName: string,
) {
  {
    const command = `./mvn clean compile sql:execute --define database.mysql.name=${newDatabaseName}`;
    await execa.command(command, {
      stdio: 'inherit',
      cwd: getBaseFolderPath(),
      extendEnv: true,
    });
  }
  {
    const command = `./mvn clean compile sql:execute --define database.mysql.name=${oldDatabaseName}`;
    await execa.command(command, {
      stdio: 'inherit',
      cwd: getBaseFolderPath(),
      extendEnv: true,
    });
  }
}

function getBaseFolderPath() {
  return path.normalize(path.join(path.resolve('.')));
}

async function getUnusedPort() {
  return await getPort();
}

async function existFolder(folderPath: string) {
  try {
    return (await fs.promises.stat(folderPath)).isDirectory();
  } catch {
    await fs.promises.rm(folderPath, { recursive: true, force: true });
    return false;
  }
}

export default main();
