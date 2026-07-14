import { useNavigate } from 'react-router-dom';

// Section 12 color coding, matching EvaluationCard: green 8-10, yellow 5-7, red 1-4.
const scoreClasses = (score) => {
  if (score >= 8) return 'bg-green-100 text-green-700';
  if (score >= 5) return 'bg-yellow-100 text-yellow-700';
  return 'bg-red-100 text-red-700';
};

const scoreTextClasses = (score) => {
  if (score >= 8) return 'text-green-600';
  if (score >= 5) return 'text-yellow-600';
  return 'text-red-600';
};

const hasScore = (score) => typeof score === 'number' && !Number.isNaN(score);

const formatDuration = (totalSeconds) => {
  if (!totalSeconds && totalSeconds !== 0) return null;
  const m = Math.floor(totalSeconds / 60);
  const s = totalSeconds % 60;
  return `${m}m ${s}s`;
};

// summary: full InterviewSummaryDto (from POST /end) or a lightweight
// { interviewId, interviewType, difficulty, date, durationSeconds, overallScore }
// record from GET /api/history when the page is opened without full detail.
const InterviewSummary = ({ summary, isLightweight }) => {
  const navigate = useNavigate();
  const duration = formatDuration(summary.durationSeconds);

  return (
    <div className="max-w-2xl mx-auto flex flex-col gap-6">
      <div className="text-center">
        <p className="text-sm font-medium text-slate-500">
          {summary.interviewType?.replace('_', ' ')} · {summary.difficulty}
          {duration ? ` · ${duration}` : ''}
        </p>
        <h1 className="mt-1 text-2xl font-bold text-slate-800">Interview Complete</h1>
      </div>

      {/* Large overall + confidence score display */}
      <div className="grid grid-cols-2 gap-4">
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 text-center">
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
            Overall Score
          </p>
          <p className={`mt-2 text-5xl font-bold ${scoreTextClasses(summary.overallScore)}`}>
            {summary.overallScore.toFixed(1)}
            <span className="text-lg text-slate-400">/10</span>
          </p>
        </div>
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 text-center">
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
            Confidence Score
          </p>
          {hasScore(summary.confidenceScore) ? (
            <p className={`mt-2 text-5xl font-bold ${scoreTextClasses(summary.confidenceScore)}`}>
              {summary.confidenceScore.toFixed(1)}
              <span className="text-lg text-slate-400">/10</span>
            </p>
          ) : (
            <p className="mt-2 text-5xl font-bold text-slate-300">—</p>
          )}
        </div>
      </div>

      {isLightweight && (
        <div className="bg-slate-50 border border-slate-200 rounded-lg px-4 py-3 text-sm text-slate-500 text-center">
          Detailed per-question feedback is only available right after the interview ends.
          This is a summary from your interview history.
        </div>
      )}

      {/* Per-question breakdown (Section 8.4: question text + overall/confidence per question) */}
      {!isLightweight && summary.questionResults?.length > 0 && (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
            Per-Question Breakdown
          </h2>
          <ul className="mt-4 flex flex-col gap-3">
            {summary.questionResults.map((q) => (
              <li
                key={q.questionNumber}
                className="border border-slate-200 rounded-lg p-4 flex items-start justify-between gap-4"
              >
                <div>
                  <p className="text-xs font-medium text-slate-400">
                    Question {q.questionNumber}
                  </p>
                  <p className="mt-1 text-sm text-slate-800">{q.questionText}</p>
                </div>
                <div className="flex gap-2 shrink-0">
                  <span
                    className={`px-2.5 py-1 rounded-full text-xs font-semibold ${scoreClasses(q.overallScore)}`}
                  >
                    {q.overallScore.toFixed(1)}
                  </span>
                  <span
                    className={`px-2.5 py-1 rounded-full text-xs font-semibold ${scoreClasses(q.confidenceScore)}`}
                  >
                    {q.confidenceScore.toFixed(1)}
                  </span>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Improvement suggestions */}
      {!isLightweight && summary.improvementSuggestions?.length > 0 && (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
            Improvement Suggestions
          </h2>
          <ul className="mt-3 flex flex-col gap-2">
            {summary.improvementSuggestions.map((suggestion, i) => (
              <li key={i} className="flex items-start gap-2 text-sm text-slate-600">
                <span className="text-indigo-500 mt-0.5">•</span>
                {suggestion}
              </li>
            ))}
          </ul>
        </div>
      )}

      <div className="flex flex-col sm:flex-row gap-3">
        <button
          type="button"
          onClick={() => navigate('/dashboard')}
          className="flex-1 py-2.5 text-sm font-medium text-slate-600 border border-slate-300 rounded-lg hover:bg-slate-100 transition-colors"
        >
          Back to Dashboard
        </button>
        <button
          type="button"
          onClick={() => navigate('/interviews/setup')}
          className="flex-1 py-2.5 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition-colors"
        >
          Start New Interview
        </button>
      </div>
    </div>
  );
};

export default InterviewSummary;
