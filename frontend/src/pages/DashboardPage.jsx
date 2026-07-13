import { Link, Navigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import useApi from '../hooks/useApi';
import useAuth from '../hooks/useAuth';
import LoadingSpinner from '../components/common/LoadingSpinner';

const NAV_CARDS = [
  {
    to: '/interviews/setup',
    icon: '🎤',
    title: 'Start Mock Interview',
    description: 'Practice HR, resume-based, or CS fundamentals interviews by voice',
    primary: true,
  },
  {
    to: '/roadmaps',
    icon: '🗺️',
    title: 'Career Roadmaps',
    description: 'Browse curated learning roadmaps and pick your path',
  },
  {
    to: '/recommendations',
    icon: '💡',
    title: 'AI Skill Recommendations',
    description: 'Get suggestions for the next skills to learn',
  },
  {
    to: '/skill-tracker',
    icon: '✅',
    title: 'Skill Tracker',
    description: 'Check off your daily progress on the active roadmap',
  },
  {
    to: '/analytics',
    icon: '📊',
    title: 'Analytics',
    description: 'Track your interview scores and confidence trends',
  },
  {
    to: '/history',
    icon: '🕘',
    title: 'Interview History',
    description: 'Review your last 5 interviews',
  },
];

const DashboardPage = () => {
  const { get } = useApi();
  const { user } = useAuth();

  const {
    data: profileRes,
    isLoading: profileLoading,
    isError: profileMissing,
  } = useQuery({
    queryKey: ['profile'],
    queryFn: () => get('/profile'),
    retry: false,
  });

  const { data: analyticsRes } = useQuery({
    queryKey: ['analytics'],
    queryFn: () => get('/analytics'),
    retry: false,
    enabled: !!profileRes?.data,
  });

  const { data: roadmapRes, isError: noRoadmap } = useQuery({
    queryKey: ['myRoadmap'],
    queryFn: () => get('/roadmaps/my'),
    retry: false,
    enabled: !!profileRes?.data,
  });

  if (profileLoading) {
    return <LoadingSpinner message="Loading your dashboard..." />;
  }

  // Section 12: if the user has no profile yet, send them to profile setup.
  if (profileMissing) {
    return <Navigate to="/profile-setup" replace />;
  }

  const analytics = analyticsRes?.data;
  const roadmap = noRoadmap ? null : roadmapRes?.data;
  const latestScore =
    analytics?.recentScores?.length > 0
      ? analytics.recentScores[analytics.recentScores.length - 1].overallScore
      : null;

  // Only the email is available from the auth response; greet with its prefix.
  const displayName = user?.email ? user.email.split('@')[0] : 'there';

  return (
    <div className="max-w-5xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800">Welcome back, {displayName} 👋</h1>
      <p className="mt-1 text-sm text-slate-500">
        Ready for your next practice session? Pick up where you left off.
      </p>

      {/* Quick stats summary */}
      <div className="mt-6 grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wide">
            Interviews Completed
          </p>
          <p className="mt-1 text-3xl font-bold text-slate-800">
            {analytics ? analytics.totalInterviews : '—'}
          </p>
        </div>
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wide">Latest Score</p>
          <p className="mt-1 text-3xl font-bold text-slate-800">
            {latestScore !== null ? `${latestScore.toFixed(1)}/10` : '—'}
          </p>
        </div>
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wide">
            Current Roadmap
          </p>
          <p className="mt-1 text-lg font-semibold text-slate-800 truncate">
            {roadmap ? roadmap.title : 'None selected'}
          </p>
          {!roadmap && (
            <Link to="/roadmaps" className="text-xs text-indigo-600 hover:text-indigo-700">
              Browse roadmaps →
            </Link>
          )}
        </div>
      </div>

      {/* Navigation cards */}
      <div className="mt-8 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {NAV_CARDS.map((card) => (
          <Link
            key={card.to}
            to={card.to}
            className={`rounded-xl p-6 shadow-sm border transition-all hover:shadow-md hover:-translate-y-0.5 ${
              card.primary
                ? 'bg-indigo-600 border-indigo-600 text-white'
                : 'bg-white border-slate-200'
            }`}
          >
            <div className="text-3xl">{card.icon}</div>
            <h2
              className={`mt-3 text-base font-semibold ${
                card.primary ? 'text-white' : 'text-slate-800'
              }`}
            >
              {card.title}
            </h2>
            <p className={`mt-1 text-sm ${card.primary ? 'text-indigo-100' : 'text-slate-500'}`}>
              {card.description}
            </p>
          </Link>
        ))}
      </div>
    </div>
  );
};

export default DashboardPage;
