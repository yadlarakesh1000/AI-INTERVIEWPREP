import { useQuery } from '@tanstack/react-query';
import useApi from '../hooks/useApi';
import ScoreCard from '../components/analytics/ScoreCard';
import ScoreChart from '../components/analytics/ScoreChart';
import ImprovementSuggestions from '../components/analytics/ImprovementSuggestions';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';

// Improvement status badge styling (Section 9 statuses).
const STATUS_STYLES = {
  IMPROVING: 'bg-green-100 text-green-700',
  DECLINING: 'bg-red-100 text-red-700',
  STABLE: 'bg-yellow-100 text-yellow-700',
  NOT_ENOUGH_DATA: 'bg-slate-100 text-slate-500',
};

const STATUS_LABELS = {
  IMPROVING: 'Improving',
  DECLINING: 'Declining',
  STABLE: 'Stable',
  NOT_ENOUGH_DATA: 'Not enough data',
};

const AnalyticsPage = () => {
  const { get } = useApi();

  const {
    data: analyticsRes,
    isLoading,
    isError,
    refetch,
  } = useQuery({
    queryKey: ['analytics'],
    queryFn: () => get('/analytics'),
  });

  if (isLoading) {
    return <LoadingSpinner message="Loading your analytics..." />;
  }

  if (isError) {
    return <ErrorMessage message="Could not load analytics." onRetry={refetch} />;
  }

  const analytics = analyticsRes.data;
  const status = analytics.improvementStatus || 'NOT_ENOUGH_DATA';

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800">Analytics</h1>
      <p className="mt-1 text-sm text-slate-500">
        Your interview performance across your last 5 completed interviews.
      </p>

      {/* Score cards + improvement status */}
      <div className="mt-6 grid grid-cols-1 sm:grid-cols-3 gap-4">
        <ScoreCard
          label="Avg Overall Score"
          value={analytics.averageOverallScore.toFixed(1)}
          suffix="/10"
        />
        <ScoreCard
          label="Avg Confidence Score"
          value={analytics.averageConfidenceScore.toFixed(1)}
          suffix="/10"
        />
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 text-center flex flex-col justify-center">
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
            Improvement Status
          </p>
          <div className="mt-2">
            <span
              className={`inline-block px-3 py-1 rounded-full text-sm font-semibold ${STATUS_STYLES[status]}`}
            >
              {STATUS_LABELS[status]}
            </span>
          </div>
        </div>
      </div>

      {/* Line chart */}
      <div className="mt-6">
        <ScoreChart recentScores={analytics.recentScores} />
      </div>

      {/* Latest improvement suggestions */}
      <div className="mt-6">
        <ImprovementSuggestions suggestions={analytics.latestSuggestions} />
      </div>
    </div>
  );
};

export default AnalyticsPage;
