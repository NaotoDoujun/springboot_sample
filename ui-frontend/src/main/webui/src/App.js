import * as React from 'react';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import './App.css';
import { AppSettingsContext, Home, Results } from './ components';

const lightTheme = createTheme({
  palette: {
    mode: 'light'
  },
});

const darkTheme = createTheme({
  palette: {
    mode: 'dark',
  },
});

function App() {
  const isDark = useMediaQuery('(prefers-color-scheme: dark)');
  const [fruits, setFruits] = React.useState([]);
  const [files, setFiles] = React.useState([]);
  return (
    <ThemeProvider theme={isDark ? darkTheme : lightTheme}>
      <CssBaseline />
      <AppSettingsContext.Provider value={{ fruits, setFruits, files, setFiles }}>
        <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />}>
            <Route index element={<Results />} />
          </Route>
        </Routes>
        </BrowserRouter>
      </AppSettingsContext.Provider>
    </ThemeProvider>
  );
}

export default App;
