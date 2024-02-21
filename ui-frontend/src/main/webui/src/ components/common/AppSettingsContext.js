import * as React from 'react';

export const AppSettingsContext = React.createContext({
  isOpenDrawer: false,
  setIsOpenDrawer: () => undefined
});