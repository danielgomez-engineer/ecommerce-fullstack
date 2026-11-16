import { useEffect } from 'react';
import { Alert, Box, Button, Chip, CircularProgress, Divider, IconButton, Paper, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import RemoveIcon from '@mui/icons-material/Remove';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../../app/hooks';
import { clearOrderFeedback, createOrderThunk } from '../orders/ordersSlice';
import { clearCart, removeFromCart, updateQuantity } from './cartSlice';

export const CartPage = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const items = useAppSelector((state) => state.cart.items);
  const user = useAppSelector((state) => state.auth.user);
  const { creating, error } = useAppSelector((state) => state.orders);

  useEffect(() => {
    dispatch(clearOrderFeedback());
  }, [dispatch]);

  const totalItems = items.reduce((sum, item) => sum + item.quantity, 0);
  const total = items.reduce((sum, item) => sum + item.product.price * item.quantity, 0);

  const handlePlaceOrder = async () => {
    if (!user) {
      navigate('/login');
      return;
    }
    if (user.role !== 'CUSTOMER') return;
    const result = await dispatch(
      createOrderThunk({
        items: items.map((i) => ({ productId: i.product.id, quantity: i.quantity })),
      })
    );
    if (createOrderThunk.fulfilled.match(result)) {
      dispatch(clearCart());
      navigate('/orders');
    }
  };

  if (items.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
        <Typography variant="h5" sx={{ fontWeight: 600 }}>
          Your cart is empty
        </Typography>
        <Typography color="text.secondary">Browse the catalog and add some products.</Typography>
        <Button variant="contained" onClick={() => navigate('/products')}>
          Back to Products
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <Typography variant="h4" sx={{ fontWeight: 700 }}>
        Shopping Cart
      </Typography>
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: { xs: '1fr', md: '2fr 1fr' },
          gap: 3,
          alignItems: 'start',
        }}
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          {items.map((item) => (
            <Paper key={item.product.id} sx={{ p: 2 }}>
              <Box
                sx={{
                  display: 'grid',
                  gridTemplateColumns: { xs: '80px 1fr', sm: '100px 1fr auto' },
                  gap: 2,
                  alignItems: 'center',
                }}
              >
                <Box
                  component="img"
                  src={item.product.imageUrl || 'https://placehold.co/400x300?text=No+Image'}
                  alt={item.product.name}
                  sx={{ width: '100%', height: 80, objectFit: 'cover', borderRadius: 1 }}
                />
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                    {item.product.name}
                  </Typography>
                  <Box>
                    <Chip label={item.product.categoryName} size="small" variant="outlined" />
                  </Box>
                  <Typography variant="body2" color="text.secondary">
                    {`$${item.product.price.toFixed(2)} each`}
                  </Typography>
                </Box>
                <Box
                  sx={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 1.5,
                    gridColumn: { xs: '1 / -1', sm: 'auto' },
                    justifyContent: { xs: 'space-between', sm: 'flex-end' },
                  }}
                >
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                    <IconButton
                      size="small"
                      aria-label="decrease quantity"
                      onClick={() =>
                        dispatch(updateQuantity({ productId: item.product.id, quantity: item.quantity - 1 }))
                      }
                    >
                      <RemoveIcon fontSize="small" />
                    </IconButton>
                    <Typography sx={{ minWidth: 24, textAlign: 'center' }}>{item.quantity}</Typography>
                    <IconButton
                      size="small"
                      aria-label="increase quantity"
                      disabled={item.quantity >= item.product.stock}
                      onClick={() =>
                        dispatch(updateQuantity({ productId: item.product.id, quantity: item.quantity + 1 }))
                      }
                    >
                      <AddIcon fontSize="small" />
                    </IconButton>
                  </Box>
                  <Typography sx={{ fontWeight: 700, minWidth: 80, textAlign: 'right' }}>
                    {`$${(item.product.price * item.quantity).toFixed(2)}`}
                  </Typography>
                  <IconButton
                    color="error"
                    aria-label="remove item"
                    onClick={() => dispatch(removeFromCart(item.product.id))}
                  >
                    <DeleteIcon />
                  </IconButton>
                </Box>
              </Box>
            </Paper>
          ))}
        </Box>

        <Paper sx={{ p: 3, display: 'flex', flexDirection: 'column', gap: 2 }}>
          <Typography variant="h6" sx={{ fontWeight: 700 }}>
            Order Summary
          </Typography>
          <Divider />
          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
            <Typography color="text.secondary">Items</Typography>
            <Typography>{totalItems}</Typography>
          </Box>
          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
            <Typography variant="h6">Total</Typography>
            <Typography variant="h6" color="primary" sx={{ fontWeight: 700 }}>
              {`$${total.toFixed(2)}`}
            </Typography>
          </Box>
          {user && user.role !== 'CUSTOMER' && (
            <Alert severity="warning">Only customer accounts can place orders.</Alert>
          )}
          {error && <Alert severity="error">{error}</Alert>}
          <Button
            variant="contained"
            size="large"
            disabled={creating || (!!user && user.role !== 'CUSTOMER')}
            onClick={handlePlaceOrder}
            startIcon={creating ? <CircularProgress size={18} color="inherit" /> : undefined}
          >
            Place Order
          </Button>
        </Paper>
      </Box>
    </Box>
  );
};
