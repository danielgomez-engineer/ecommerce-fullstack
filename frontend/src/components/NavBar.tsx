import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import StorefrontIcon from '@mui/icons-material/Storefront';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { logout } from '../features/auth/authSlice';

export const NavBar = () => {
  const { user } = useAppSelector((state) => state.auth);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  return (
    <AppBar position="sticky">
      <Toolbar sx={{ justifyContent: 'space-between' }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <StorefrontIcon />
          <Typography
            variant="h6"
            component={RouterLink}
            to="/products"
            sx={{ color: 'inherit', textDecoration: 'none', fontWeight: 700 }}
          >
            E-Commerce Store
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <Button color="inherit" component={RouterLink} to="/products">
            Products
          </Button>
          <Button
            color="inherit"
            component={RouterLink}
            to="/cart"
            startIcon={<ShoppingCartIcon />}
          >
            Cart
          </Button>

          {user ? (
            <>
              <Button color="inherit" component={RouterLink} to="/orders">
                My Orders
              </Button>
              <Typography variant="body2" sx={{ display: { xs: 'none', sm: 'block' }, mx: 1 }}>
                Hi, {user.fullName}
              </Typography>
              <Button color="inherit" variant="outlined" size="small" onClick={handleLogout}>
                Logout
              </Button>
            </>
          ) : (
            <>
              <Button color="inherit" component={RouterLink} to="/login">
                Login
              </Button>
              <Button color="secondary" variant="contained" size="small" component={RouterLink} to="/register">
                Register
              </Button>
            </>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};