import { Navigate, Route, Routes } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { ProtectedRoute } from '../components/ProtectedRoute';
import { LoginPage } from '../features/auth/LoginPage';
import { RegisterPage } from '../features/auth/RegisterPage';
import { ProductDetailPage } from '../features/products/ProductDetailPage';
import { ProductsPage } from '../features/products/ProductsPage';
import { Typography } from '@mui/material';

export const AppRoutes = () => {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<Navigate to="/products" replace />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/products" element={<ProductsPage />} />
        <Route path="/products/:id" element={<ProductDetailPage />} />
        <Route path="/cart" element={<Typography variant="h5">Shopping Cart (Phase 8)</Typography>} />

        <Route element={<ProtectedRoute />}>
          <Route path="/orders" element={<Typography variant="h5">Order History (Phase 8)</Typography>} />
        </Route>
      </Route>
    </Routes>
  );
};
