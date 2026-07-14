import { useParams, useLocation } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import useApi from '../hooks/useApi';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import InterviewSummary from '../components/interview/InterviewSummary';

const InterviewResultPage = () => {
  const { id } = useParams();
  const location = useLocation();
  const { get } = useApi();

  // Primary path: the full InterviewSummaryDto (per-question scores +
  // improvement suggestions) arrives via router state from InterviewScreen's
  // POST /interviews/{id}/end call. There is no GET /interviews/{id}
  // endpoint — session Q&A and transcripts are never persisted (Section 9) —
  // so a full reload of this page cannot recover that detail.
  const fullSummary = location.state?.interviewId === Number(id) ? location.state : null;

  // Fallback: look the interview up in history for a lightweight summary
  // (type, difficulty, date, duration, overall score only).
  const {
    data: historyRes,
    isLoading,
    isError,
    refetch,
  } = useQuery({
    queryKey: ['history'],
    queryFn: () => get('/history'),
    enabled: !fullSummary,
  });

  if (fullSummary) {
    return (
      <div>
        <InterviewSummary summary={fullSummary} isLightweight={false} />
      </div>
    );
  }

  if (isLoading) {
    return <LoadingSpinner message="Loading your interview results..." />;
  }

  if (isError) {
    return <ErrorMessage message="Could not load this interview's results." onRetry={refetch} />;
  }

  const historyRecord = historyRes?.data?.find((h) => h.interviewId === Number(id));

  if (!historyRecord) {
    return (
      <ErrorMessage message="We couldn't find results for this interview. It may be older than your last 5 completed interviews." />
    );
  }

  return (
    <div>
      <InterviewSummary
        summary={{
          interviewId: historyRecord.interviewId,
          interviewType: historyRecord.interviewType,
          difficulty: historyRecord.difficulty,
          durationSeconds: historyRecord.durationSeconds,
          overallScore: historyRecord.overallScore,
          // InterviewHistoryDto (Section 8.9) does not carry a confidence
          // score — only the full end-of-interview response does.
          confidenceScore: null,
        }}
        isLightweight
      />
    </div>
  );
};

export default InterviewResultPage;
