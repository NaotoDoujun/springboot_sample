import * as React from 'react';

export const AppSettingsContext = React.createContext({
  fruits: [],
  setFruits: () => undefined,
  files: [],
  setFiles: () => undefined
});