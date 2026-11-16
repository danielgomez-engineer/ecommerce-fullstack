import { Navigate, Outlet } from 'react-router-dom';
import { useAppSelector } from '../app/hooks';

export const ProtectedRoute = () => {
  const { token } = useAppSelector((state) => state.auth);
  return token ? <Outlet /> : <Navigate to="/login" replace />;
};