import axiosClient from './axiosClient';
import type { GeneralResponse, Order, OrderRequest, PageResponse } from '../types';

export const orderApi = {
  createOrder: async (data: OrderRequest): Promise<Order> => {
    const res = await axiosClient.post<GeneralResponse<Order>>('/orders', data);
    return res.data.data;
  },
  getMyOrders: async (page = 0, size = 10): Promise<PageResponse<Order>> => {
    const res = await axiosClient.get<GeneralResponse<PageResponse<Order>>>('/orders/me', {
      params: { page, size },
    });
    return res.data.data;
  },
  getOrderById: async (id: number): Promise<Order> => {
    const res = await axiosClient.get<GeneralResponse<Order>>(`/orders/${id}`);
    return res.data.data;
  },
};
