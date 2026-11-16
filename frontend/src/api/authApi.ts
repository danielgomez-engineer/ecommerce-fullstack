import axiosClient from './axiosClient';
import type { AuthResponse, GeneralResponse, LoginRequest, RegisterRequest, User } from '../types';

export const authApi = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const res = await axiosClient.post<GeneralResponse<AuthResponse>>('/auth/login', data);
    return res.data.data;
  },
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const res = await axiosClient.post<GeneralResponse<AuthResponse>>('/auth/register', data);
    return res.data.data;
  },
  getMe: async (): Promise<User> => {
    const res = await axiosClient.get<GeneralResponse<User>>('/auth/me');
    return res.data.data;
  },
};