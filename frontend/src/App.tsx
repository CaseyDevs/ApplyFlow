import { useEffect, useState } from 'react'
import './App.css'
import ApplicationsPage from './pages/ApplicationsPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import { useAuth } from './context/AuthContext'
import Navbar from './components/Navbar'

function App() {
  const {loading, user } = useAuth();
  const [isLoggedIn, setIsLoggedIn] = useState<Boolean>(false); 

  useEffect(() => {
    setIsLoggedIn(user != null);
  }, [user])

  if (loading) return <p>Loading...</p>;

  return (
    <>
      <Navbar />
      {isLoggedIn 
        ? <ApplicationsPage /> 
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
