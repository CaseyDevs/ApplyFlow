import '../src/styles/global.css';
import Navbar from './components/navbar/Navbar';
import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import AboutPage from './pages/AboutPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ApplicationsPage from './pages/ApplicationsPage';
import CreateApplicationPage from './pages/CreateApplicationPage';
import JobBoardPage from './pages/JobBoardPage';
import CreateJobBoardPage from './pages/CreateJobBoardPage';
import JobBoardDetailsPage from './pages/JobBoardDetailsPage';
import UpdateApplicationPage from './pages/UpdateApplicationPage';

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/about" element={<AboutPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/applications" element={<ApplicationsPage />} />
        <Route path="/applications/:applicationId" element={<UpdateApplicationPage />} />
        <Route path="/create-application" element={<CreateApplicationPage />} />
        <Route path="/job-boards/:jobBoardId/applications/:applicationId" element={<UpdateApplicationPage />} />
        <Route path="/job-boards/:jobBoardId" element={<JobBoardDetailsPage />} />
        <Route path="/job-boards" element={<JobBoardPage />} />
        <Route path="/job-boards/create" element={<CreateJobBoardPage />} />
      </Routes>
    </>
  );
}

export default App;
