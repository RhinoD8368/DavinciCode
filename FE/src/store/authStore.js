import { create } from 'zustand';

const useAuthStore = create((set) => ({
  accessToken: null,
  userInfo: null,
  isAuthenticated: false, 

  setLogin: (token, user) => set({ 
    accessToken: token, 
    userInfo: user, 
    isAuthenticated: true, 
  }),

  setLogout: () => set({ 
    accessToken: null, 
    userInfo: null, 
    isAuthenticated: false, 
  }),
}));

export default useAuthStore;