import { useQuery } from '@tanstack/react-query';
import useApi from '../hooks/useApi';
import HistoryList from '../components/history/HistoryList';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';

const HistoryPage = () => {
  const { get } = useApi();

  const {
    data: historyRes,
    isLoading,
    isError,
    refetch,
  } = useQuery({
    queryKey: ['history'],
    queryFn: () => get('/history'),
  });

  return (
    <div className="max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800">Interview History</h1>
      <p className="mt-1 text-sm text-slate-500">
        Your last 5 completed interviews. Select one to see its results.
      </p>

      <div className="mt-6">
        {isLoading ? (
          <LoadingSpinner message="Loading your interview history..." />
        ) : isError ? (
          <ErrorMessage message="Could not load your interview history." onRetry={refetch} />
        ) : (
          <HistoryList interviews={historyRes?.data} />
        )}
      </div>
    </div>
  );
};

export default HistoryPage;
