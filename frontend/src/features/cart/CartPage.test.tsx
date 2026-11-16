import { beforeEach, describe, expect, it } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import authReducer from '../auth/authSlice';
import ordersReducer from '../orders/ordersSlice';
import cartReducer from './cartSlice';
import { CartPage } from './CartPage';
import type { CartItem } from '../../types';

const buildStore = (items: CartItem[]) =>
  configureStore({
    reducer: { auth: authReducer, orders: ordersReducer, cart: cartReducer },
    preloadedState: { cart: { items } },
  });

const renderPage = (items: CartItem[]) =>
  render(
    <Provider store={buildStore(items)}>
      <MemoryRouter>
        <CartPage />
      </MemoryRouter>
    </Provider>
  );

describe('CartPage', () => {
  beforeEach(() => {
    cleanup();
    localStorage.clear();
  });

  it('shows the empty cart message and a link to explore products', () => {
    renderPage([]);
    expect(screen.getByText('Your cart is empty')).toBeDefined();
    expect(screen.getByRole('button', { name: /back to products/i })).toBeDefined();
  });

  it('shows product names and the computed total', () => {
    renderPage([
      {
        product: {
          id: 1,
          name: 'Mechanical Keyboard',
          price: 50,
          stock: 10,
          categoryId: 1,
          categoryName: 'Electronics',
        },
        quantity: 2,
      },
      {
        product: {
          id: 2,
          name: 'Wireless Mouse',
          price: 25.5,
          stock: 10,
          categoryId: 1,
          categoryName: 'Electronics',
        },
        quantity: 1,
      },
    ]);
    expect(screen.getByText('Mechanical Keyboard')).toBeDefined();
    expect(screen.getByText('Wireless Mouse')).toBeDefined();
    expect(screen.getByText('$125.50')).toBeDefined();
    expect(screen.getByRole('button', { name: /place order/i })).toBeDefined();
  });
});
