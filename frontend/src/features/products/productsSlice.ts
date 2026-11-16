import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import {
  createProduct,
  deleteProduct,
  getCategories,
  getProductById,
  getProducts,
  updateProduct,
} from '../../api/productApi';
import { getErrorMessage } from '../../api/axiosClient';
import type { Category, Product, ProductFilters, ProductRequest } from '../../types';

interface ProductsState {
  items: Product[];
  selectedProduct: Product | null;
  categories: Category[];
  page: number;
  totalPages: number;
  totalElements: number;
  loading: boolean;
  error: string | null;
}

const initialState: ProductsState = {
  items: [],
  selectedProduct: null,
  categories: [],
  page: 0,
  totalPages: 0,
  totalElements: 0,
  loading: false,
  error: null,
};

export const fetchProducts = createAsyncThunk(
  'products/fetchAll',
  async (filters: ProductFilters, { rejectWithValue }) => {
    try {
      return await getProducts(filters);
    } catch (err) {
      return rejectWithValue(getErrorMessage(err));
    }
  }
);

export const fetchProductById = createAsyncThunk(
  'products/fetchById',
  async (id: number, { rejectWithValue }) => {
    try {
      return await getProductById(id);
    } catch (err) {
      return rejectWithValue(getErrorMessage(err));
    }
  }
);

export const fetchCategories = createAsyncThunk(
  'products/fetchCategories',
  async (_: void, { rejectWithValue }) => {
    try {
      return await getCategories();
    } catch (err) {
      return rejectWithValue(getErrorMessage(err));
    }
  }
);

export const createProductThunk = createAsyncThunk(
  'products/create',
  async (data: ProductRequest, { rejectWithValue }) => {
    try {
      return await createProduct(data);
    } catch (err) {
      return rejectWithValue(getErrorMessage(err));
    }
  }
);

export const updateProductThunk = createAsyncThunk(
  'products/update',
  async (payload: { id: number; data: ProductRequest }, { rejectWithValue }) => {
    try {
      return await updateProduct(payload.id, payload.data);
    } catch (err) {
      return rejectWithValue(getErrorMessage(err));
    }
  }
);

export const deleteProductThunk = createAsyncThunk(
  'products/delete',
  async (id: number, { rejectWithValue }) => {
    try {
      await deleteProduct(id);
      return id;
    } catch (err) {
      return rejectWithValue(getErrorMessage(err));
    }
  }
);

const productsSlice = createSlice({
  name: 'products',
  initialState,
  reducers: {
    clearProductsError: (state) => {
      state.error = null;
    },
    clearSelectedProduct: (state) => {
      state.selectedProduct = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchProducts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProducts.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content;
        state.page = action.payload.page;
        state.totalPages = action.payload.totalPages;
        state.totalElements = action.payload.totalElements;
      })
      .addCase(fetchProducts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(fetchProductById.pending, (state) => {
        state.loading = true;
        state.error = null;
        state.selectedProduct = null;
      })
      .addCase(fetchProductById.fulfilled, (state, action) => {
        state.loading = false;
        state.selectedProduct = action.payload;
      })
      .addCase(fetchProductById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(fetchCategories.fulfilled, (state, action) => {
        state.categories = action.payload;
      })
      .addCase(fetchCategories.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      .addCase(createProductThunk.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      .addCase(updateProductThunk.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      .addCase(deleteProductThunk.rejected, (state, action) => {
        state.error = action.payload as string;
      });
  },
});

export const { clearProductsError, clearSelectedProduct } = productsSlice.actions;
export default productsSlice.reducer;
