import { useEffect, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Snackbar,
  TextField,
  Typography,
} from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../../app/hooks';
import { clearSelectedProduct, fetchProductById } from './productsSlice';

export const ProductDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { selectedProduct: product, loading, error } = useAppSelector((state) => state.products);
  const [quantity, setQuantity] = useState(1);
  const [added, setAdded] = useState(false);

  useEffect(() => {
    const numericId = Number(id);
    if (Number.isFinite(numericId)) {
      dispatch(fetchProductById(numericId));
    }
    return () => {
      dispatch(clearSelectedProduct());
    };
  }, [dispatch, id]);

  useEffect(() => {
    setQuantity(1);
  }, [product?.id]);

  const handleQuantity = (value: string) => {
    if (!product) return;
    const parsed = Math.floor(Number(value));
    if (Number.isNaN(parsed)) {
      setQuantity(1);
      return;
    }
    setQuantity(Math.min(Math.max(parsed, 1), Math.max(product.stock, 1)));
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error || !product) {
    return (
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, alignItems: 'flex-start' }}>
        <Alert severity="error" sx={{ width: '100%' }}>
          {error || 'Product not found'}
        </Alert>
        <Button variant="outlined" onClick={() => navigate('/products')}>
          Back to Products
        </Button>
      </Box>
    );
  }

  const inStock = product.stock > 0;

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <Box>
        <Button variant="outlined" onClick={() => navigate('/products')}>
          Back to Products
        </Button>
      </Box>
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' },
          gap: 4,
        }}
      >
        <Box
          component="img"
          src={product.imageUrl || 'https://placehold.co/400x300?text=No+Image'}
          alt={product.name}
          sx={{
            width: '100%',
            maxHeight: 480,
            objectFit: 'cover',
            borderRadius: 2,
          }}
        />
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
            <Chip label={product.categoryName} variant="outlined" />
            <Chip
              label={inStock ? `In Stock (${product.stock})` : 'Out of Stock'}
              color={inStock ? 'success' : 'error'}
            />
          </Box>
          <Typography variant="h4" sx={{ fontWeight: 700 }}>
            {product.name}
          </Typography>
          <Typography variant="h5" color="primary" sx={{ fontWeight: 700 }}>
            {`$${product.price.toFixed(2)}`}
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ whiteSpace: 'pre-line' }}>
            {product.description || 'No description available.'}
          </Typography>
          <Typography variant="body2">{`Available stock: ${product.stock}`}</Typography>
          <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', flexWrap: 'wrap' }}>
            <TextField
              label="Quantity"
              type="number"
              size="small"
              value={quantity}
              disabled={!inStock}
              onChange={(e) => handleQuantity(e.target.value)}
              slotProps={{ htmlInput: { min: 1, max: product.stock, step: 1 } }}
              sx={{ width: 120 }}
            />
            <Button variant="contained" size="large" disabled={!inStock} onClick={() => setAdded(true)}>
              Add to Cart
            </Button>
          </Box>
        </Box>
      </Box>
      <Snackbar
        open={added}
        autoHideDuration={3000}
        onClose={() => setAdded(false)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert severity="success" variant="filled" onClose={() => setAdded(false)}>
          {`${quantity} x ${product.name} added to cart`}
        </Alert>
      </Snackbar>
    </Box>
  );
};
