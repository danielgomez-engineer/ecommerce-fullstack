import {
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardMedia,
  Chip,
  Typography,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch } from '../../app/hooks';
import { addToCart } from '../cart/cartSlice';
import type { Product } from '../../types';

interface ProductCardProps {
  product: Product;
  isAdmin?: boolean;
  onEdit?: (product: Product) => void;
  onDelete?: (product: Product) => void;
}

export const ProductCard = ({ product, isAdmin = false, onEdit, onDelete }: ProductCardProps) => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const inStock = product.stock > 0;

  return (
    <Card sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <CardMedia
        component="img"
        height="200"
        image={product.imageUrl || 'https://placehold.co/400x300?text=No+Image'}
        alt={product.name}
        sx={{ objectFit: 'cover' }}
      />
      <CardContent sx={{ flexGrow: 1 }}>
        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mb: 1.5 }}>
          <Chip label={product.categoryName} size="small" variant="outlined" />
          <Chip
            label={inStock ? `In Stock (${product.stock})` : 'Out of Stock'}
            size="small"
            color={inStock ? 'success' : 'error'}
          />
        </Box>
        <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
          {product.name}
        </Typography>
        <Typography
          variant="body2"
          color="text.secondary"
          sx={{
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden',
            mb: 1.5,
          }}
        >
          {product.description}
        </Typography>
        <Typography variant="h6" color="primary" sx={{ fontWeight: 700 }}>
          {`$${product.price.toFixed(2)}`}
        </Typography>
      </CardContent>
      <CardActions sx={{ justifyContent: 'space-between', px: 2, pb: 2 }}>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button size="small" variant="contained" onClick={() => navigate(`/products/${product.id}`)}>
            Details
          </Button>
          <Button
            size="small"
            variant="outlined"
            disabled={!inStock || isAdmin}
            onClick={() => dispatch(addToCart({ product, quantity: 1 }))}
          >
            Add to Cart
          </Button>
        </Box>
        {isAdmin && (
          <Box sx={{ display: 'flex', gap: 0.5 }}>
            <Button size="small" onClick={() => onEdit?.(product)}>
              Edit
            </Button>
            <Button size="small" color="error" onClick={() => onDelete?.(product)}>
              Delete
            </Button>
          </Box>
        )}
      </CardActions>
    </Card>
  );
};
