import { useCallback, useEffect, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  MenuItem,
  Pagination,
  TextField,
  Typography,
} from '@mui/material';
import { useAppDispatch, useAppSelector } from '../../app/hooks';
import type { Product, ProductFilters, ProductRequest } from '../../types';
import { ProductCard } from './ProductCard';
import { ProductFormDialog } from './ProductFormDialog';
import {
  clearProductsError,
  createProductThunk,
  deleteProductThunk,
  fetchCategories,
  fetchProducts,
  updateProductThunk,
} from './productsSlice';

const PAGE_SIZE = 12;

const emptyFilters: ProductFilters = {
  name: '',
  categoryId: '',
  minPrice: '',
  maxPrice: '',
};

export const ProductsPage = () => {
  const dispatch = useAppDispatch();
  const user = useAppSelector((state) => state.auth.user);
  const { items, categories, page, totalPages, loading, error } = useAppSelector(
    (state) => state.products
  );
  const isAdmin = user?.role === 'ADMIN';

  const [draft, setDraft] = useState<ProductFilters>(emptyFilters);
  const [applied, setApplied] = useState<ProductFilters>(emptyFilters);
  const [currentPage, setCurrentPage] = useState(0);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Product | null>(null);
  const [toDelete, setToDelete] = useState<Product | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const load = useCallback(() => {
    dispatch(fetchProducts({ ...applied, page: currentPage, size: PAGE_SIZE }));
  }, [dispatch, applied, currentPage]);

  useEffect(() => {
    dispatch(clearProductsError());
    dispatch(fetchCategories());
  }, [dispatch]);

  useEffect(() => {
    load();
  }, [load]);

  const handleSearch = () => {
    setCurrentPage(0);
    setApplied({ ...draft });
  };

  const handleClear = () => {
    setDraft(emptyFilters);
    setApplied(emptyFilters);
    setCurrentPage(0);
  };

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const openEdit = (product: Product) => {
    setEditing(product);
    setFormOpen(true);
  };

  const closeForm = () => {
    if (!submitting) {
      setFormOpen(false);
      setEditing(null);
    }
  };

  const handleSubmit = async (data: ProductRequest) => {
    setSubmitting(true);
    const result = editing
      ? await dispatch(updateProductThunk({ id: editing.id, data }))
      : await dispatch(createProductThunk(data));
    setSubmitting(false);
    if (result.meta.requestStatus === 'fulfilled') {
      setFormOpen(false);
      setEditing(null);
      load();
    }
  };

  const confirmDelete = async () => {
    if (!toDelete) return;
    const target = toDelete;
    setToDelete(null);
    const result = await dispatch(deleteProductThunk(target.id));
    if (result.meta.requestStatus === 'fulfilled') {
      if (items.length === 1 && currentPage > 0) {
        setCurrentPage(currentPage - 1);
      } else {
        load();
      }
    }
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: 2,
        }}
      >
        <Typography variant="h4" sx={{ fontWeight: 700 }}>
          Products
        </Typography>
        {isAdmin && (
          <Button variant="contained" onClick={openCreate}>
            + New Product
          </Button>
        )}
      </Box>

      <Box
        component="form"
        onSubmit={(e) => {
          e.preventDefault();
          handleSearch();
        }}
        sx={{
          display: 'grid',
          gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', md: '2fr 1.5fr 1fr 1fr auto' },
          gap: 2,
          alignItems: 'center',
        }}
      >
        <TextField
          label="Search by name"
          size="small"
          value={draft.name ?? ''}
          onChange={(e) => setDraft({ ...draft, name: e.target.value })}
        />
        <TextField
          select
          label="Category"
          size="small"
          value={draft.categoryId ?? ''}
          onChange={(e) =>
            setDraft({ ...draft, categoryId: e.target.value === '' ? '' : Number(e.target.value) })
          }
        >
          <MenuItem value="">All Categories</MenuItem>
          {categories.map((category) => (
            <MenuItem key={category.id} value={category.id}>
              {category.name}
            </MenuItem>
          ))}
        </TextField>
        <TextField
          label="Min Price"
          size="small"
          type="number"
          slotProps={{ htmlInput: { min: 0, step: '0.01' } }}
          value={draft.minPrice ?? ''}
          onChange={(e) => setDraft({ ...draft, minPrice: e.target.value })}
        />
        <TextField
          label="Max Price"
          size="small"
          type="number"
          slotProps={{ htmlInput: { min: 0, step: '0.01' } }}
          value={draft.maxPrice ?? ''}
          onChange={(e) => setDraft({ ...draft, maxPrice: e.target.value })}
        />
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button type="submit" variant="contained">
            Search
          </Button>
          <Button variant="outlined" onClick={handleClear}>
            Clear
          </Button>
        </Box>
      </Box>

      {error && (
        <Alert severity="error" onClose={() => dispatch(clearProductsError())}>
          {error}
        </Alert>
      )}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      ) : items.length === 0 && !error ? (
        <Box sx={{ textAlign: 'center', py: 8 }}>
          <Typography variant="h6" color="text.secondary">
            No products found
          </Typography>
        </Box>
      ) : (
        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', md: 'repeat(3, 1fr)' },
            gap: 3,
          }}
        >
          {items.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              isAdmin={isAdmin}
              onEdit={openEdit}
              onDelete={setToDelete}
            />
          ))}
        </Box>
      )}

      {totalPages > 1 && (
        <Box sx={{ display: 'flex', justifyContent: 'center' }}>
          <Pagination
            count={totalPages}
            page={page + 1}
            color="primary"
            onChange={(_, value) => setCurrentPage(value - 1)}
          />
        </Box>
      )}

      <ProductFormDialog
        open={formOpen}
        product={editing}
        categories={categories}
        submitting={submitting}
        onClose={closeForm}
        onSubmit={handleSubmit}
      />

      <Dialog open={!!toDelete} onClose={() => setToDelete(null)}>
        <DialogTitle>Delete product</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {`Are you sure you want to delete "${toDelete?.name ?? ''}"? This action cannot be undone.`}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setToDelete(null)}>Cancel</Button>
          <Button color="error" variant="contained" onClick={confirmDelete}>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
