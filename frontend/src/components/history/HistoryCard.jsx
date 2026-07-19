import { useNavigate } from 'react-router-dom';

// Same score colour coding used across the app: green 8-10, yellow 5-7, red 1-4.
const scoreClasses = (score) => {
  if (score >= 8) return 'bg-green-100 text-green-700';
  if (score >= 5) return 'bg-yellow-100 text-yellow-700';
  return 'bg-red-100 text-red-700';
};

const TYPE_LABELS = {
  HR: 'HR & Communication',
  RESUME_BASED: 'Resume-Based',
  CS_FUNDAMENTALS: 'CS Fundamentals',
};

const DIFFICULTY_LABELS = {
  EASY: 'Easy',
  MEDIUM: 'Medium',
};

const formatDuration = (totalSeconds) => {
  if (totalSeconds === null || totalSeconds === undefined) return '—';
  const m = Math.floor(totalSeconds / 60);
  const s = totalSeconds % 60;
  return `${m}m ${s}s`;
};

const HistoryCard = ({ interview }) => {
  const navigate = useNavigate();

  return (
    <button
      type="button"
      onClick={() => navigate(`/interviews/${interview.interviewId}/result`)}
      className="w-full text-left bg-white rounded-xl border border-slate-200 shadow-sm p-5 transition-all hover:shadow-md hover:border-indigo-300 flex items-center justify-between gap-4"
    >
      <div className="min-w-0">
        <p className="text-sm font-semibold text-slate-800">
          {TYPE_LABELS[interview.interviewType] || interview.interviewType}
        </p>
        <p className="mt-1 text-xs text-slate-500">
          {DIFFICULTY_LABELS[interview.difficulty] || interview.difficulty} · {interview.date} ·{' '}
          {formatDuration(interview.durationSeconds)}
        </p>
      </div>
      <div className="flex items-center gap-3 shrink-0">
        <span
          className={`px-3 py-1 rounded-full text-sm font-semibold ${scoreClasses(interview.overallScore)}`}
        >
          {interview.overallScore.toFixed(1)}
        </span>
        <span className="text-slate-300">›</span>
      </div>
    </button>
  );
};

export default HistoryCard;
