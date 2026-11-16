import { beforeEach, describe, expect, it } from 'vitest';
import reducer, { addToCart, clearCart, removeFromCart, updateQuantity } from './cartSlice';
import type { Product } from '../../types';

const makeProduct = (overrides: Partial<Product> = {}): Product => ({
  id: 1,
  name: 'Keyboard',
  price: 50,
  stock: 5,
  categoryId: 1,
  categoryName: 'Electronics',
  ...overrides,
});

const empty = { items: [] };

describe('cartSlice', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('adds a new product to the cart', () => {
    const state = reducer(empty, addToCart({ product: makeProduct(), quantity: 2 }));
    expect(state.items).toHaveLength(1);
    expect(state.items[0].quantity).toBe(2);
    expect(state.items[0].product.id).toBe(1);
  });

  it('sums the quantity when the product already exists', () => {
    let state = reducer(empty, addToCart({ product: makeProduct(), quantity: 1 }));
    state = reducer(state, addToCart({ product: makeProduct(), quantity: 3 }));
    expect(state.items).toHaveLength(1);
    expect(state.items[0].quantity).toBe(4);
  });

  it('never exceeds the available stock when adding', () => {
    let state = reducer(empty, addToCart({ product: makeProduct({ stock: 3 }), quantity: 10 }));
    expect(state.items[0].quantity).toBe(3);
    state = reducer(state, addToCart({ product: makeProduct({ stock: 3 }), quantity: 2 }));
    expect(state.items[0].quantity).toBe(3);
  });

  it('does not add products without stock', () => {
    const state = reducer(empty, addToCart({ product: makeProduct({ stock: 0 }), quantity: 1 }));
    expect(state.items).toHaveLength(0);
  });

  it('never exceeds the available stock when updating quantity', () => {
    let state = reducer(empty, addToCart({ product: makeProduct({ stock: 4 }), quantity: 1 }));
    state = reducer(state, updateQuantity({ productId: 1, quantity: 99 }));
    expect(state.items[0].quantity).toBe(4);
    state = reducer(state, updateQuantity({ productId: 1, quantity: 2 }));
    expect(state.items[0].quantity).toBe(2);
  });

  it('removes the item when the updated quantity is zero or less', () => {
    let state = reducer(empty, addToCart({ product: makeProduct(), quantity: 2 }));
    state = reducer(state, updateQuantity({ productId: 1, quantity: 0 }));
    expect(state.items).toHaveLength(0);
  });

  it('removes a product and persists the change in localStorage', () => {
    let state = reducer(empty, addToCart({ product: makeProduct({ id: 1 }), quantity: 1 }));
    state = reducer(state, addToCart({ product: makeProduct({ id: 2, name: 'Mouse' }), quantity: 1 }));
    state = reducer(state, removeFromCart(1));
    expect(state.items).toHaveLength(1);
    expect(state.items[0].product.id).toBe(2);
    const stored = JSON.parse(localStorage.getItem('cart') as string);
    expect(stored).toHaveLength(1);
    expect(stored[0].product.id).toBe(2);
  });

  it('clears the cart and removes the localStorage entry', () => {
    let state = reducer(empty, addToCart({ product: makeProduct(), quantity: 2 }));
    expect(localStorage.getItem('cart')).toBeTruthy();
    state = reducer(state, clearCart());
    expect(state.items).toEqual([]);
    expect(localStorage.getItem('cart')).toBe(null);
  });
});
