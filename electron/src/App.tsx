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
import { zhCN } from 'date-fns/locale'

export default (<div className="w-screen h-screen overflow-auto">
  <div style={{ display: "flex", minWidth: "100%", height: "100%" }}>
    <div className='flex flex-col flex-auto' style={{ height: "max-content", minHeight: "100%" }}>
      <CssBaseline />
      <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={zhCN}  >
        <I18nComponent>
          <GlobalMessageComponent />
          {Router}
        </I18nComponent>
      </LocalizationProvider>
    </div>
  </div>
</div>);