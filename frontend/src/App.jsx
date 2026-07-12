import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './components/common/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProfileSetupPage from './pages/ProfileSetupPage';
import DashboardPage from './pages/DashboardPage';
import InterviewSetupPage from './pages/InterviewSetupPage';
import InterviewPage from './pages/InterviewPage';
import InterviewResultPage from './pages/InterviewResultPage';
import ResumeUploadPage from './pages/ResumeUploadPage';
import RoadmapListPage from './pages/RoadmapListPage';
import RoadmapDetailPage from './pages/RoadmapDetailPage';
import SkillTrackerPage from './pages/SkillTrackerPage';
import SkillRecommendationPage from './pages/SkillRecommendationPage';
import AnalyticsPage from './pages/AnalyticsPage';
import HistoryPage from './pages/HistoryPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Protected routes — rendered inside the app shell (Navbar + Sidebar) */}
        <Route element={<ProtectedRoute />}>
          <Route path="/profile-setup" element={<ProfileSetupPage />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/interviews/setup" element={<InterviewSetupPage />} />
          <Route path="/interviews/:id" element={<InterviewPage />} />
          <Route path="/interviews/:id/result" element={<InterviewResultPage />} />
          <Route path="/resume/upload" element={<ResumeUploadPage />} />
          <Route path="/roadmaps" element={<RoadmapListPage />} />
          <Route path="/roadmaps/:id" element={<RoadmapDetailPage />} />
          <Route path="/skill-tracker" element={<SkillTrackerPage />} />
          <Route path="/recommendations" element={<SkillRecommendationPage />} />
          <Route path="/analytics" element={<AnalyticsPage />} />
          <Route path="/history" element={<HistoryPage />} />
        </Route>

        {/* Default and unknown routes */}
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
