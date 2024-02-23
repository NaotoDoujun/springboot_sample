import * as React from 'react';
import axios from 'axios';
import { Box, Paper, Button, TextField, Typography } from '@mui/material';

function Commit() {
    const [message, setMessage] = React.useState('');

    const handleOnChange = (event) => {
        setMessage(event.target.value);
    };

    const handleOnClick = (event) => {
        const formData = new FormData();
        formData.append('commit', message);
        axios.post(`${window.API_BASE_URL}/api/git/commit`, formData, {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded'
            }
          }).then(response => {
            console.log('ok',response);
          }).catch(error => {
            console.error('error',error);
          });
    };

    return (
      <Box sx={{
        display: 'flex',
        flexWrap: 'wrap',
        '& > :not(style)': {
          mt: 1, p: 2
        },
      }}>
        <Paper>
          <Typography variant="h5">Commit</Typography>
          <TextField id="message" label="message" variant="outlined" onChange={handleOnChange} />
          <Button variant="contained" sx={{ m: 1 }} onClick={handleOnClick}>Commit</Button>
        </Paper>
      </Box>
    );
};

export { Commit };