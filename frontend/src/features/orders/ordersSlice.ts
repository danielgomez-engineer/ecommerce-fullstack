import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { orderApi } from '../../api/orderApi';
import { getErrorMessage } from '../../api/axiosClient';
import type { Order, OrderRequest } from '../../types';

interface OrdersState {
  items: Order[];
  page: number;
  totalPages: number;
  totalElements: number;
  loading: boolean;
  creating: boolean;
  error: string | null;
  lastCreatedOrder: Order | null;
}

const initialState: OrdersState = {
  items: [],
  page: 0,
  totalPages: 0,
  totalElements: 0,
  loading: false,
  creating: false,
  error: null,
  lastCreatedOrder: null,
};

export const createOrderThunk = createAsyncThunk(
  'orders/create',
  async (payload: OrderRequest, { rejectWithValue }) => {
    try {
      return await orderApi.createOrder(payload);
    } catch (err) {
      return rejectWithValue(getErrorMessage(err));
    }
  }
);

export const fetchMyOrders = createAsyncThunk(
  'orders/fetchMine',
  async ({ page, size }: { page?: number; size?: number }, { rejectWithValue }) => {
    try {
      return await orderApi.getMyOrders(page, size);
    } catch (err) {
      return rejectWithValue(getErrorMessage(err));
    }
  }
);

const ordersSlice = createSlice({
  name: 'orders',
  initialState,
  reducers: {
    clearOrderFeedback: (state) => {
      state.error = null;
      state.lastCreatedOrder = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(createOrderThunk.pending, (state) => {
        state.creating = true;
        state.error = null;
        state.lastCreatedOrder = null;
      })
      .addCase(createOrderThunk.fulfilled, (state, action) => {
        state.creating = false;
        state.lastCreatedOrder = action.payload;
      })
      .addCase(createOrderThunk.rejected, (state, action) => {
        state.creating = false;
        state.error = action.payload as string;
      })
      .addCase(fetchMyOrders.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyOrders.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content;
        state.page = action.payload.page;
        state.totalPages = action.payload.totalPages;
        state.totalElements = action.payload.totalElements;
      })
      .addCase(fetchMyOrders.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { clearOrderFeedback } = ordersSlice.actions;
export default ordersSlice.reducer;
