import '../src/styles/global.css';
import Navbar from './components/navbar/Navbar';
import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ApplicationsPage from './pages/ApplicationsPage';
import CreateApplicationPage from './pages/CreateApplicationPage';
import JobBoardPage from './pages/JobBoardPage';
import CreateJobBoardPage from './pages/CreateJobBoardPage';
import JobBoardDetailsPage from './pages/JobBoardDetailsPage';
import UpdateApplicationPage from './pages/UpdateApplicationPage';
import EmailVerifiedSuccessPage from './pages/EmailVerifiedSuccessPage';
import EmailVerifyPage from './pages/EmailVerifyPage';
import EmailVerifiedFailurePage from './pages/EmailVerifiedFailurePage';
import InvitationConfirmationPage from './pages/InvitationConfirmationPage';

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/applications" element={<ApplicationsPage />} />
        <Route path="/applications/:applicationId" element={<UpdateApplicationPage />} />
        <Route path="/create-application" element={<CreateApplicationPage />} />
        <Route path="/job-boards/:jobBoardId/applications/:applicationId" element={<UpdateApplicationPage />} />
        <Route path="/job-boards/:jobBoardId" element={<JobBoardDetailsPage />} />
        <Route path="/job-boards" element={<JobBoardPage />} />
        <Route path="/job-boards/create" element={<CreateJobBoardPage />} />
        <Route path="/email-verified-success" element={<EmailVerifiedSuccessPage />} />
        <Route path="/email-verified-failure" element={<EmailVerifiedFailurePage />} />
        <Route path="/email-verify" element={<EmailVerifyPage />} />
        <Route path="/job-boards/:jobBoardId/invitation" element={<InvitationConfirmationPage />} />

      </Routes>
    </>
  );
}

export default App;
