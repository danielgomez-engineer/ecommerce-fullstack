import { useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  TextField,
} from '@mui/material';
import type { Category, Product, ProductRequest } from '../../types';

const productSchema = z.object({
  name: z.string().min(1, 'Name is required').max(150, 'Name is too long'),
  description: z.string().optional(),
  price: z.number({ error: 'Price is required' }).positive('Price must be greater than 0'),
  stock: z
    .number({ error: 'Stock is required' })
    .int('Stock must be a whole number')
    .min(0, 'Stock cannot be negative'),
  imageUrl: z.string().optional(),
  categoryId: z.number({ error: 'Category is required' }).int().positive('Category is required'),
});

type ProductFormData = z.infer<typeof productSchema>;

interface ProductFormDialogProps {
  open: boolean;
  product: Product | null;
  categories: Category[];
  submitting?: boolean;
  onClose: () => void;
  onSubmit: (data: ProductRequest) => void;
}

const emptyValues = {
  name: '',
  description: '',
  price: undefined,
  stock: undefined,
  imageUrl: '',
  categoryId: undefined,
};

export const ProductFormDialog = ({
  open,
  product,
  categories,
  submitting = false,
  onClose,
  onSubmit,
}: ProductFormDialogProps) => {
  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ProductFormData>({
    resolver: zodResolver(productSchema),
    defaultValues: emptyValues,
  });

  useEffect(() => {
    if (!open) return;
    if (product) {
      reset({
        name: product.name,
        description: product.description ?? '',
        price: product.price,
        stock: product.stock,
        imageUrl: product.imageUrl ?? '',
        categoryId: product.categoryId,
      });
    } else {
      reset(emptyValues);
    }
  }, [open, product, reset]);

  const submit = (data: ProductFormData) => {
    onSubmit({
      name: data.name.trim(),
      description: data.description?.trim() || undefined,
      price: data.price,
      stock: data.stock,
      imageUrl: data.imageUrl?.trim() || undefined,
      categoryId: data.categoryId,
    });
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <form onSubmit={handleSubmit(submit)} noValidate>
        <DialogTitle>{product ? 'Edit Product' : 'New Product'}</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
            <TextField
              label="Name"
              fullWidth
              {...register('name')}
              error={!!errors.name}
              helperText={errors.name?.message}
            />
            <TextField
              label="Description"
              fullWidth
              multiline
              minRows={3}
              {...register('description')}
              error={!!errors.description}
              helperText={errors.description?.message}
            />
            <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
              <TextField
                label="Price"
                type="number"
                slotProps={{ htmlInput: { step: '0.01', min: 0 } }}
                {...register('price', { valueAsNumber: true })}
                error={!!errors.price}
                helperText={errors.price?.message}
              />
              <TextField
                label="Stock"
                type="number"
                slotProps={{ htmlInput: { step: 1, min: 0 } }}
                {...register('stock', { valueAsNumber: true })}
                error={!!errors.stock}
                helperText={errors.stock?.message}
              />
            </Box>
            <TextField
              label="Image URL"
              fullWidth
              {...register('imageUrl')}
              error={!!errors.imageUrl}
              helperText={errors.imageUrl?.message}
            />
            <Controller
              name="categoryId"
              control={control}
              render={({ field }) => (
                <TextField
                  select
                  label="Category"
                  fullWidth
                  value={field.value ?? ''}
                  onChange={(e) => field.onChange(e.target.value === '' ? undefined : Number(e.target.value))}
                  onBlur={field.onBlur}
                  inputRef={field.ref}
                  error={!!errors.categoryId}
                  helperText={errors.categoryId?.message}
                >
                  {categories.map((category) => (
                    <MenuItem key={category.id} value={category.id}>
                      {category.name}
                    </MenuItem>
                  ))}
                </TextField>
              )}
            />
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={onClose} disabled={submitting}>
            Cancel
          </Button>
          <Button type="submit" variant="contained" disabled={submitting}>
            {product ? 'Save Changes' : 'Create'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};
