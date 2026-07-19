import { useQuery } from '@tanstack/react-query';
import useApi from '../hooks/useApi';
import SkillRecommendations from '../components/recommendations/SkillRecommendations';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';

const SkillRecommendationPage = () => {
  const { get } = useApi();

  const {
    data: recRes,
    isLoading,
    isFetching,
    isError,
    refetch,
  } = useQuery({
    queryKey: ['skillRecommendations'],
    queryFn: () => get('/skill-recommendations'),
    // The LLM produces fresh suggestions each call; don't auto-refetch,
    // let the user pull new ones with the Refresh button.
    staleTime: Infinity,
    retry: false,
  });

  return (
    <div className="max-w-3xl mx-auto">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">AI Skill Recommendations</h1>
          <p className="mt-1 text-sm text-slate-500">
            Suggested next skills based on your current stack and career goal.
          </p>
        </div>
        <button
          type="button"
          onClick={() => refetch()}
          disabled={isFetching}
          className="shrink-0 px-4 py-2 text-sm font-medium text-indigo-600 border border-indigo-200 rounded-lg hover:bg-indigo-50 disabled:opacity-60 disabled:cursor-not-allowed transition-colors"
        >
          {isFetching ? 'Refreshing...' : 'Refresh Recommendations'}
        </button>
      </div>

      <div className="mt-6">
        {isLoading ? (
          <LoadingSpinner message="Generating your recommendations..." />
        ) : isError ? (
          <ErrorMessage
            message="Could not generate recommendations. Please try again."
            onRetry={refetch}
          />
        ) : (
          <SkillRecommendations data={recRes.data} />
        )}
      </div>
    </div>
  );
};

export default SkillRecommendationPage;
