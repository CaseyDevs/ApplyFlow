import './App.css';
import Navbar from './components/navbar/Navbar';
import { Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ApplicationsPage from './pages/ApplicationsPage';
import CreateApplicationPage from './pages/CreateApplicationPage';
import JobBoardPage from './pages/JobBoardPage';

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/applications" element={<ApplicationsPage />} />
        <Route path="/create-application" element={<CreateApplicationPage />} />
        <Route path="/job-boards" element={<JobBoardPage />} />
      </Routes>
    </>
  );
}

export default App;
