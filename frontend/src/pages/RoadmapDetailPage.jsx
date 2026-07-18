import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import useApi from '../hooks/useApi';
import RoadmapDetail from '../components/roadmap/RoadmapDetail';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';

const RoadmapDetailPage = () => {
  const { id } = useParams();
  const { get, post } = useApi();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [isSelecting, setIsSelecting] = useState(false);
  const [selectError, setSelectError] = useState('');

  const {
    data: roadmapRes,
    isLoading,
    isError,
    refetch,
  } = useQuery({
    queryKey: ['roadmap', id],
    queryFn: () => get(`/roadmaps/${id}`),
  });

  const { data: myRoadmapRes } = useQuery({
    queryKey: ['myRoadmap'],
    queryFn: () => get('/roadmaps/my'),
    retry: false,
  });

  const roadmap = roadmapRes?.data;
  const isActive = myRoadmapRes?.data?.id === Number(id);

  const handleSelect = async () => {
    if (!roadmap) return;
    setIsSelecting(true);
    setSelectError('');
    try {
      await post('/roadmaps/select', {
        roadmapTemplateId: roadmap.id,
        selectedDurationWeeks: roadmap.durationWeeks,
      });
      // The active roadmap changed — refresh anything keyed on it.
      await queryClient.invalidateQueries({ queryKey: ['myRoadmap'] });
      navigate('/skill-tracker');
    } catch (err) {
      setSelectError(err.message);
      setIsSelecting(false);
    }
  };

  if (isLoading) {
    return <LoadingSpinner message="Loading roadmap..." />;
  }

  if (isError || !roadmap) {
    return <ErrorMessage message="Could not load this roadmap." onRetry={refetch} />;
  }

  return (
    <RoadmapDetail
      roadmap={roadmap}
      isActive={isActive}
      onSelect={handleSelect}
      isSelecting={isSelecting}
      selectError={selectError}
    />
  );
};

export default RoadmapDetailPage;
