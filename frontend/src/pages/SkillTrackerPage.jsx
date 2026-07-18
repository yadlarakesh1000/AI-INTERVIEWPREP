import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import useApi from '../hooks/useApi';
import WeekView, { ProgressBar } from '../components/skilltracker/WeekView';
import LoadingSpinner from '../components/common/LoadingSpinner';

const SkillTrackerPage = () => {
  const { get, put } = useApi();
  const queryClient = useQueryClient();

  const [currentWeek, setCurrentWeek] = useState(1);
  const [togglingDay, setTogglingDay] = useState(null);

  // Overview also acts as the "has an active roadmap?" gate — it 400s when
  // there is none (backend: "Select a roadmap first...").
  const {
    data: overviewRes,
    isLoading: overviewLoading,
    isError: noRoadmap,
  } = useQuery({
    queryKey: ['skillOverview'],
    queryFn: () => get('/skill-tracker/overview'),
    retry: false,
  });

  const {
    data: weekRes,
    isLoading: weekLoading,
    isFetching: weekFetching,
  } = useQuery({
    queryKey: ['skillWeek', currentWeek],
    queryFn: () => get(`/skill-tracker/week/${currentWeek}`),
    retry: false,
    enabled: !noRoadmap && !overviewLoading,
  });

  const overview = overviewRes?.data;
  const week = weekRes?.data;

  const handleToggle = async (dayNumber) => {
    setTogglingDay(dayNumber);
    try {
      await put('/skill-tracker/toggle', { weekNumber: currentWeek, dayNumber });
      // Refetch the week (day state + per-week %) and overview (overall %).
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['skillWeek', currentWeek] }),
        queryClient.invalidateQueries({ queryKey: ['skillOverview'] }),
      ]);
    } finally {
      setTogglingDay(null);
    }
  };

  if (overviewLoading) {
    return <LoadingSpinner message="Loading your skill tracker..." />;
  }

  // No active roadmap — empty state (Section 12).
  if (noRoadmap) {
    return (
      <div className="max-w-3xl mx-auto text-center py-16">
        <div className="text-4xl">🗺️</div>
        <h1 className="mt-3 text-xl font-semibold text-slate-800">Select a roadmap first</h1>
        <p className="mt-1 text-sm text-slate-500">
          The skill tracker follows your active roadmap. Choose one to start tracking your daily
          progress.
        </p>
        <Link
          to="/roadmaps"
          className="inline-block mt-5 px-6 py-2.5 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition-colors"
        >
          Browse Roadmaps
        </Link>
      </div>
    );
  }

  const totalWeeks = overview?.totalWeeks ?? 1;

  return (
    <div className="max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800">Skill Tracker</h1>
      <p className="mt-1 text-sm text-slate-500">
        Check off each day as you complete it and watch your progress grow.
      </p>

      {/* Overall progress */}
      {overview && (
        <div className="mt-6 bg-white rounded-xl border border-slate-200 shadow-sm p-6">
          <div className="flex items-center justify-between mb-2">
            <span className="text-sm font-semibold text-slate-700">Overall Progress</span>
            <span className="text-sm font-medium text-slate-500">
              {overview.completedDays} / {overview.totalDays} days ·{' '}
              {Math.round(overview.progressPercent || 0)}%
            </span>
          </div>
          <ProgressBar percent={overview.progressPercent} />
        </div>
      )}

      {/* Week view */}
      <div className="mt-4">
        {weekLoading || !week ? (
          <LoadingSpinner message={`Loading week ${currentWeek}...`} />
        ) : (
          <WeekView
            week={week}
            totalWeeks={totalWeeks}
            onPrev={() => setCurrentWeek((w) => Math.max(1, w - 1))}
            onNext={() => setCurrentWeek((w) => Math.min(totalWeeks, w + 1))}
            onToggleDay={handleToggle}
            togglingDay={weekFetching ? null : togglingDay}
          />
        )}
      </div>
    </div>
  );
};

export default SkillTrackerPage;
