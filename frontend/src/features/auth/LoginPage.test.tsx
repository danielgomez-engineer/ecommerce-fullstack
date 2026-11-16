import { describe, expect, it } from 'vitest';
import { fireEvent, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import { store } from '../../app/store';
import { LoginPage } from './LoginPage';

const renderPage = () =>
  render(
    <Provider store={store}>
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>
    </Provider>
  );

describe('LoginPage', () => {
  it('renders the email and password fields and the login button', () => {
    renderPage();
    expect(screen.getByLabelText(/email/i)).toBeDefined();
    expect(screen.getByLabelText(/password/i)).toBeDefined();
    expect(screen.getByRole('button', { name: /login/i })).toBeDefined();
  });

  it('shows validation messages when submitting empty fields', async () => {
    renderPage();
    fireEvent.click(screen.getByRole('button', { name: /login/i }));
    expect(await screen.findByText('Enter a valid email address')).toBeTruthy();
    expect(await screen.findByText('Password is required')).toBeTruthy();
  });
});
