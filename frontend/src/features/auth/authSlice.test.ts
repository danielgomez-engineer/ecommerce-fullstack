import { beforeEach, describe, expect, it } from 'vitest';
import reducer, { logout } from './authSlice';
import type { User } from '../../types';

const user: User = {
  id: 1,
  email: 'customer@demo.com',
  fullName: 'Jane Customer',
  role: 'CUSTOMER',
};

describe('authSlice', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('has an empty initial state without a session', () => {
    const state = reducer(undefined, { type: 'unknown' });
    expect(state.user).toBe(null);
    expect(state.token).toBe(null);
    expect(state.loading).toBe(false);
    expect(state.error).toBe(null);
  });

  it('clears user, token and localStorage on logout', () => {
    localStorage.setItem('token', 'abc');
    localStorage.setItem('user', JSON.stringify(user));
    const state = reducer(
      { user, token: 'abc', loading: false, error: 'boom' },
      logout()
    );
    expect(state.user).toBe(null);
    expect(state.token).toBe(null);
    expect(state.error).toBe(null);
    expect(localStorage.getItem('token')).toBe(null);
    expect(localStorage.getItem('user')).toBe(null);
  });
});
