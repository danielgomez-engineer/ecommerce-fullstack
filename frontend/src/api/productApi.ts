import axiosClient from './axiosClient';
import type {
  Category,
  GeneralResponse,
  PageResponse,
  Product,
  ProductFilters,
  ProductRequest,
} from '../types';

export const getProducts = async (filters: ProductFilters): Promise<PageResponse<Product>> => {
  const params: Record<string, string | number> = {};
  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      params[key] = value;
    }
  });
  const res = await axiosClient.get<GeneralResponse<PageResponse<Product>>>('/products', { params });
  return res.data.data;
};

export const getProductById = async (id: number): Promise<Product> => {
  const res = await axiosClient.get<GeneralResponse<Product>>(`/products/${id}`);
  return res.data.data;
};

export const getCategories = async (): Promise<Category[]> => {
  const res = await axiosClient.get<GeneralResponse<Category[]>>('/categories');
  return res.data.data;
};

export const createProduct = async (data: ProductRequest): Promise<Product> => {
  const res = await axiosClient.post<GeneralResponse<Product>>('/products', data);
  return res.data.data;
};

export const updateProduct = async (id: number, data: ProductRequest): Promise<Product> => {
  const res = await axiosClient.put<GeneralResponse<Product>>(`/products/${id}`, data);
  return res.data.data;
};

export const deleteProduct = async (id: number): Promise<void> => {
  await axiosClient.delete(`/products/${id}`);
};
