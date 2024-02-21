import { AppBar, Toolbar, Box, Button, Grid, Typography } from '@mui/material';
import { Outlet } from 'react-router-dom';

function Home() {

  const handleLogout = () => {
    window.location.href = "https://dev-i7btdyfxq64m44re.us.auth0.com/v2/logout?client_id=dGTb7mv3Z1VAttIb1uJSx5qoRPaaw4nA&returnTo=http://localhost:8080/logout"
  };

  return (
      <Box sx={{ flexGrow: 1 }}>
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                UI-Frontend Sample
            </Typography>
            <Button color="inherit" onClick={handleLogout}>Logout</Button>
          </Toolbar>
        </AppBar>
        <Grid container spacing={2} sx={{ p: 2 }}>
          <Grid item>
            <Outlet />
          </Grid>
        </Grid>
      </Box>
  );
};

export { Home };