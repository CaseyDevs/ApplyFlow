import { useEffect, useState } from 'react'
import './App.css'
import ApplicationsPage from './pages/ApplicationsPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import { useAuth } from './context/AuthContext'
import { logoutUser } from './api/auth/logout'

function App() {
  const {loading, user, refreshUser } = useAuth();
  const [isLoggedIn, setIsLoggedIn] = useState<Boolean>(false); 

  useEffect(() => {
    if (user != null) {
      setIsLoggedIn(true)
    }
  }, [user])

  async function handleLogout() {
    try {
      await logoutUser;
      await refreshUser;
      setIsLoggedIn(false);
    } catch (err) {
      return err;
    }
  }

  return (
    <>
      {loading && <p>Loading...</p>}
      {isLoggedIn 
        ? 
        <>
          <ApplicationsPage /> 
          <button onClick={handleLogout}>Logout</button>
        </>
        : 
        <>
          <RegisterPage />
          <LoginPage />
        </>
      }
    </>
  )
}

export default App
