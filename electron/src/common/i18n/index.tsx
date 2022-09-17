import WindowsLocale from 'windows-locale';
import zh_CN_JSON from '@/i18n/zh-CN.json';
import { observable } from 'mobx-react-use-autorun';
import en_US_JSON from '@/i18n/en-US.json'

export const I18nEnum: Record<string, {
  id: string,
  json: any,
  language: string,
}> = {
  "zh-CN": {
    id: "zh-CN",
    json: zh_CN_JSON,
    language: WindowsLocale["zh-cn"].language,
  },
  "en-US": {
    id: "en-US",
    json: en_US_JSON,
    language: WindowsLocale["en-us"].language,
  },
  "zh-SG": {
    id: "zh-SG",
    json: zh_CN_JSON,
    language: WindowsLocale["zh-sg"].language,
  }
}

const state = observable({
  I18nLocale: I18nEnum['en-US'].id,
});

if (Object.keys(I18nEnum).includes(window.navigator.language)) {
  state.I18nLocale = window.navigator.language;
}

export const useI18nLocale = (): string => {
  return ((I18nEnum as any)[state.I18nLocale] as typeof I18nEnum["en-US"]).id;
}

export const useI18nJson = () => {
  return ((I18nEnum as any)[state.I18nLocale] as typeof I18nEnum["en-US"]).json;
}

export const setI18nLocale = (locale: string) => {
  if (Object.keys(I18nEnum).includes(locale)) {
    state.I18nLocale = locale;
  }
};


