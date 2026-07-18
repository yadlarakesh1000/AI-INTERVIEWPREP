import { useQuery } from '@tanstack/react-query';
import useApi from '../hooks/useApi';
import RoadmapList from '../components/roadmap/RoadmapList';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';

const RoadmapListPage = () => {
  const { get } = useApi();

  const {
    data: roadmapsRes,
    isLoading,
    isError,
    refetch,
  } = useQuery({
    queryKey: ['roadmaps'],
    queryFn: () => get('/roadmaps'),
  });

  // The user's active roadmap (if any) — used to badge it in the list.
  // A 404 (no active roadmap) is expected and non-fatal.
  const { data: myRoadmapRes } = useQuery({
    queryKey: ['myRoadmap'],
    queryFn: () => get('/roadmaps/my'),
    retry: false,
  });

  const activeRoadmapId = myRoadmapRes?.data?.id ?? null;

  return (
    <div className="max-w-5xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800">Career Roadmaps</h1>
      <p className="mt-1 text-sm text-slate-500">
        Pick a structured learning path and track your progress week by week.
      </p>

      <div className="mt-6">
        {isLoading ? (
          <LoadingSpinner message="Loading roadmaps..." />
        ) : isError ? (
          <ErrorMessage message="Could not load roadmaps." onRetry={refetch} />
        ) : (
          <RoadmapList roadmaps={roadmapsRes?.data} activeRoadmapId={activeRoadmapId} />
        )}
      </div>
    </div>
  );
};

export default RoadmapListPage;
