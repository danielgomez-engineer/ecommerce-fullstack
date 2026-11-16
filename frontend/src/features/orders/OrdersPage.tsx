import { useEffect } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Divider,
  Pagination,
  Paper,
  Typography,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../../app/hooks';
import type { Order } from '../../types';
import { clearOrderFeedback, fetchMyOrders } from './ordersSlice';

const PAGE_SIZE = 10;

const statusColor: Record<Order['status'], 'info' | 'success' | 'primary' | 'error'> = {
  CREATED: 'info',
  PAID: 'success',
  SHIPPED: 'primary',
  CANCELLED: 'error',
};

const formatDate = (value: string) =>
  new Date(value).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' });

export const OrdersPage = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { items, page, totalPages, loading, error, lastCreatedOrder } = useAppSelector(
    (state) => state.orders
  );

  useEffect(() => {
    dispatch(fetchMyOrders({ page: 0, size: PAGE_SIZE }));
    return () => {
      dispatch(clearOrderFeedback());
    };
  }, [dispatch]);

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <Typography variant="h4" sx={{ fontWeight: 700 }}>
        My Orders
      </Typography>

      {lastCreatedOrder && (
        <Alert severity="success">{`Order #${lastCreatedOrder.id} was placed successfully.`}</Alert>
      )}
      {error && <Alert severity="error">{error}</Alert>}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      ) : items.length === 0 && !error ? (
        <Box sx={{ textAlign: 'center', py: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
          <Typography variant="h6" color="text.secondary">
            You have no orders yet
          </Typography>
          <Button variant="contained" onClick={() => navigate('/products')}>
            Browse Products
          </Button>
        </Box>
      ) : (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          {items.map((order) => (
            <Paper key={order.id} sx={{ p: 3, display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Box
                sx={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  flexWrap: 'wrap',
                  gap: 1,
                }}
              >
                <Box>
                  <Typography variant="h6" sx={{ fontWeight: 600 }}>
                    {`Order #${order.id}`}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {formatDate(order.createdAt)}
                  </Typography>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Chip label={order.status} color={statusColor[order.status]} size="small" />
                  <Typography variant="h6" color="primary" sx={{ fontWeight: 700 }}>
                    {`$${order.total.toFixed(2)}`}
                  </Typography>
                </Box>
              </Box>
              <Divider />
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                {order.items.map((item) => (
                  <Box
                    key={item.productId}
                    sx={{
                      display: 'grid',
                      gridTemplateColumns: { xs: '1fr auto', sm: '2fr 1fr 1fr 1fr' },
                      gap: 1,
                      alignItems: 'center',
                    }}
                  >
                    <Typography variant="body2" sx={{ fontWeight: 500 }}>
                      {item.productName}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {`Qty: ${item.quantity}`}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {`$${item.unitPrice.toFixed(2)} each`}
                    </Typography>
                    <Typography variant="body2" sx={{ fontWeight: 600, textAlign: 'right' }}>
                      {`$${item.subtotal.toFixed(2)}`}
                    </Typography>
                  </Box>
                ))}
              </Box>
            </Paper>
          ))}
        </Box>
      )}

      {totalPages > 1 && (
        <Box sx={{ display: 'flex', justifyContent: 'center' }}>
          <Pagination
            count={totalPages}
            page={page + 1}
            color="primary"
            onChange={(_, value) => dispatch(fetchMyOrders({ page: value - 1, size: PAGE_SIZE }))}
          />
        </Box>
      )}
    </Box>
  );
};
