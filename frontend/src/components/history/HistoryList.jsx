import { Link } from 'react-router-dom';
import HistoryCard from './HistoryCard';

const HistoryList = ({ interviews }) => {
  // Empty state (Section 12).
  if (!interviews || interviews.length === 0) {
    return (
      <div className="text-center py-16">
        <div className="text-4xl">🕘</div>
        <h2 className="mt-3 text-lg font-semibold text-slate-800">
          No interviews yet. Start your first one!
        </h2>
        <p className="mt-1 text-sm text-slate-500">
          Your completed interviews will appear here with their scores.
        </p>
        <Link
          to="/interviews/setup"
          className="inline-block mt-5 px-6 py-2.5 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition-colors"
        >
          Start Mock Interview
        </Link>
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-3">
      {interviews.map((interview) => (
        <HistoryCard key={interview.interviewId} interview={interview} />
      ))}
    </div>
  );
};

export default HistoryList;
