import 'tailwindcss/utilities.css';
import '@/common/app-base-css/index.css';
import '@fontsource/roboto';
import 'reflect-metadata';
import '@/common/axios-config/AxiosConfig';
import { CssBaseline } from '@mui/material';
import Router from '@/router';
import I18nComponent from '@/common/i18n/I18nComponent';
import GlobalMessageComponent from '@/common/MessageService/GlobalMessageComponent';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { observer, useMobxState } from 'mobx-react-use-autorun';
import { I18nEnum, useI18nLocale } from '@/common/i18n'
import '@/common/StorageManage/StorageManageRun';

export default observer(() => {

  const state = useMobxState({}, {
    i18nLocale: useI18nLocale(),
  })

  return <div className="w-screen h-screen overflow-auto">
    <div style={{ display: "flex", minWidth: "100%", height: "100%" }}>
      <div className='flex flex-col flex-auto' style={{ height: "max-content", minHeight: "100%" }}>
        <CssBaseline />
        <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={I18nEnum[state.i18nLocale].DateLocale}  >
          <I18nComponent>
            <GlobalMessageComponent />
            {Router}
          </I18nComponent>
        </LocalizationProvider>
      </div>
    </div>
  </div>;
})