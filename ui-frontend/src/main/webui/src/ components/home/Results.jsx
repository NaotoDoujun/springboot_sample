import * as React from 'react';
import axios from 'axios';
import { Box, Paper, Grid, Typography } from '@mui/material';
import { AppSettingsContext } from '../common';

function Results() {
  const { fruits, setFruits, files, setFiles } = React.useContext(AppSettingsContext);
  React.useEffect(() => {
    
    axios.get(`${window.API_BASE_URL}/api/fruits`)
    .then(res => {
      setFruits(res.data);
    })
    .catch(err => {
      console.log('err:', err);
    });

    axios.get(`${window.API_BASE_URL}/api/git`)
      .then(res => {
        setFiles(res.data);
      })
      .catch(err => {
        console.log('err:', err);
      });

    }, [setFruits, setFiles]);

  return (
      <Box sx={{
        display: 'flex',
        flexWrap: 'wrap',
        '& > :not(style)': {
          m: 1, p: 2,
        },
      }}>
        <Paper>
        <Typography variant="h5">Fruits on DB</Typography>
        {fruits.map(fruit => {
          return (
            <Grid container component="li"
            key={fruit.id}
            sx={{ p: 1, borderBottom: 1, borderColor: 'divider' }}>
              <Grid item sx={{ px: 2 }}>
                <Typography variant="h6">{fruit.name}</Typography>
              </Grid>
            </Grid>
          );
        })}
        </Paper>
        <Paper>
          <Typography variant="h5">Files on Git</Typography>
          {files.map((file, index) => {
            return (
              <Grid container component="li"
              key={index}
              sx={{ p: 1, borderBottom: 1, borderColor: 'divider' }}>
                <Grid item sx={{ px: 2 }}>
                  <Typography variant="h6">{file}</Typography>
                </Grid>
              </Grid>
            );
          })}
          </Paper>
      </Box>
  );
};

export { Results };