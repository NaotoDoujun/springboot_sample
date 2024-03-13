import { AppBar, Toolbar, Box, Button, Grid, Typography } from '@mui/material';
import { Outlet } from 'react-router-dom';
import { FileDrop } from './FileDrop';
import { Commit } from './Commit';

function Home() {

  return (
      <Box sx={{ flexGrow: 1 }}>
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                UI-Frontend Sample
            </Typography>
            <form method="post" action="/logout">
              <Button type="submit" variant="contained">Logout</Button>
              <input type="hidden" name={window.csrf.parameterName} value={window.csrf.token} />
            </form>
          </Toolbar>
        </AppBar>
        <Grid container spacing={2} sx={{ p: 2 }}>
          <Grid item>
            <Outlet />
          </Grid>
          <Grid item>
            <FileDrop />
          </Grid>
          <Grid item>
            <Commit />
          </Grid>
        </Grid>
      </Box>
  );
};

export { Home };