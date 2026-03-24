import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { AuthProvider } from './context/AuthContext.tsx'
import { BrowserRouter } from 'react-router-dom'
import App from './App.tsx'
import './index.css'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // Data is considered fresh for 5 minutes.
      staleTime: 5 * 60 * 1000,
      // Keep unused query data in cache for 30 minutes.
      gcTime: 30 * 60 * 1000,
      // Avoid background refetch on tab focus while testing cache behavior.
      refetchOnWindowFocus: false,
      refetchOnReconnect: false,
    },
  },
});

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AuthProvider>
          <App />
        </AuthProvider>
      </BrowserRouter>
    </QueryClientProvider>
  </StrictMode>,
);
