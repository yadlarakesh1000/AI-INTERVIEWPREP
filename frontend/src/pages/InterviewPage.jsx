import { Navigate, useLocation } from 'react-router-dom';
import InterviewScreen from '../components/interview/InterviewScreen';

// The live voice interview. Session state (first question, TTS URL, session id)
// arrives via router state from InterviewSetup. Interview sessions are held
// in-memory on the backend, so a page refresh loses the client context —
// in that case send the user back to setup to start again.
const InterviewPage = () => {
  const location = useLocation();
  const startData = location.state;

  if (!startData?.interviewId || !startData?.question) {
    return <Navigate to="/interviews/setup" replace />;
  }

  return <InterviewScreen startData={startData} />;
};

export default InterviewPage;
