import { useState } from 'react';
import { useNavigate, Navigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import useApi from '../hooks/useApi';
import LoadingSpinner from '../components/common/LoadingSpinner';

// Aligned with the career paths used by the seeded roadmap templates.
const CAREER_PATHS = [
  'Java Full Stack Developer',
  'Python Full Stack Developer',
  'Frontend Developer',
  'Backend Developer',
  'AI Engineer',
  'Machine Learning Engineer',
  'Data Analyst',
  'Data Engineer',
  'DevOps Engineer',
  'Cloud Engineer',
  'Mobile Developer',
  'Cybersecurity Analyst',
];

const ProfileSetupPage = () => {
  const { get, post } = useApi();
  const navigate = useNavigate();

  // If the user already has a profile, this page has nothing to do.
  const {
    data: existingProfile,
    isLoading: checkingProfile,
    isError: profileMissing,
  } = useQuery({
    queryKey: ['profile'],
    queryFn: () => get('/profile'),
    retry: false,
  });

  const [skills, setSkills] = useState([]);
  const [skillInput, setSkillInput] = useState('');
  const [careerPath, setCareerPath] = useState('');
  const [studyDurationWeeks, setStudyDurationWeeks] = useState(26);
  const [dailyStudyHours, setDailyStudyHours] = useState(1);
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState('');
  const [saving, setSaving] = useState(false);

  if (checkingProfile) {
    return <LoadingSpinner message="Checking your profile..." />;
  }

  // Only redirect when the profile fetch genuinely succeeded. Checking the
  // error state too keeps this mutually exclusive with DashboardPage's
  // redirect back here — otherwise a stale cache hit plus a failed background
  // refetch bounces the user between the two pages forever.
  if (!profileMissing && existingProfile?.data) {
    return <Navigate to="/dashboard" replace />;
  }

  const addSkill = (raw) => {
    const cleaned = raw
      .split(',')
      .map((s) => s.trim())
      .filter((s) => s && !skills.some((existing) => existing.toLowerCase() === s.toLowerCase()));
    if (cleaned.length) {
      setSkills([...skills, ...cleaned]);
      setErrors({ ...errors, skills: '' });
    }
    setSkillInput('');
  };

  const handleSkillKeyDown = (e) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      addSkill(skillInput);
    }
  };

  const removeSkill = (skill) => {
    setSkills(skills.filter((s) => s !== skill));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const pendingSkills = skillInput.trim() ? [...skills, skillInput.trim()] : skills;

    const next = {};
    if (pendingSkills.length === 0) next.skills = 'Add at least one skill';
    if (!careerPath) next.careerPath = 'Select a career path';
    setErrors(next);
    if (Object.keys(next).length > 0) return;

    setSaving(true);
    setApiError('');
    try {
      await post('/profile', {
        currentSkills: pendingSkills.join(','),
        careerPath,
        studyDurationWeeks,
        dailyStudyHours,
      });
      navigate('/dashboard');
    } catch (err) {
      setApiError(err.message);
      setSaving(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800">Set Up Your Profile</h1>
      <p className="mt-1 text-sm text-slate-500">
        Tell us where you are today so we can personalize your interviews and recommendations.
      </p>

      <form onSubmit={handleSubmit} noValidate className="mt-6 bg-white rounded-xl shadow-md p-8 flex flex-col gap-6">
        <div>
          <label htmlFor="skillInput" className="block text-sm font-medium text-slate-700 mb-1">
            Current Skills
          </label>
          {skills.length > 0 && (
            <div className="flex flex-wrap gap-2 mb-2">
              {skills.map((skill) => (
                <span
                  key={skill}
                  className="inline-flex items-center gap-1 px-3 py-1 bg-indigo-50 text-indigo-700 text-sm rounded-full"
                >
                  {skill}
                  <button
                    type="button"
                    onClick={() => removeSkill(skill)}
                    aria-label={`Remove ${skill}`}
                    className="text-indigo-400 hover:text-indigo-700"
                  >
                    ×
                  </button>
                </span>
              ))}
            </div>
          )}
          <input
            id="skillInput"
            type="text"
            value={skillInput}
            onChange={(e) => setSkillInput(e.target.value)}
            onKeyDown={handleSkillKeyDown}
            onBlur={() => skillInput.trim() && addSkill(skillInput)}
            placeholder="Type a skill and press Enter (e.g. Java)"
            className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          />
          {errors.skills && <p className="mt-1 text-xs text-red-600">{errors.skills}</p>}
        </div>

        <div>
          <label htmlFor="careerPath" className="block text-sm font-medium text-slate-700 mb-1">
            Interested Career Path
          </label>
          <select
            id="careerPath"
            value={careerPath}
            onChange={(e) => {
              setCareerPath(e.target.value);
              setErrors({ ...errors, careerPath: '' });
            }}
            className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm text-slate-800 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value="">Select a career path...</option>
            {CAREER_PATHS.map((path) => (
              <option key={path} value={path}>
                {path}
              </option>
            ))}
          </select>
          {errors.careerPath && <p className="mt-1 text-xs text-red-600">{errors.careerPath}</p>}
        </div>

        <div>
          <span className="block text-sm font-medium text-slate-700 mb-2">Study Duration</span>
          <div className="flex gap-4">
            {[
              { value: 26, label: '26 weeks (6 months)' },
              { value: 52, label: '52 weeks (12 months)' },
            ].map((option) => (
              <label
                key={option.value}
                className={`flex items-center gap-2 px-4 py-2.5 border rounded-lg cursor-pointer text-sm transition-colors ${
                  studyDurationWeeks === option.value
                    ? 'border-indigo-500 bg-indigo-50 text-indigo-700 font-medium'
                    : 'border-slate-300 text-slate-600 hover:bg-slate-50'
                }`}
              >
                <input
                  type="radio"
                  name="studyDurationWeeks"
                  value={option.value}
                  checked={studyDurationWeeks === option.value}
                  onChange={() => setStudyDurationWeeks(option.value)}
                  className="accent-indigo-600"
                />
                {option.label}
              </label>
            ))}
          </div>
        </div>

        <div>
          <label htmlFor="dailyStudyHours" className="block text-sm font-medium text-slate-700 mb-1">
            Daily Study Time
          </label>
          <select
            id="dailyStudyHours"
            value={dailyStudyHours}
            onChange={(e) => setDailyStudyHours(Number(e.target.value))}
            className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm text-slate-800 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          >
            {[1, 2, 3, 4].map((h) => (
              <option key={h} value={h}>
                {h} hour{h > 1 ? 's' : ''} / day
              </option>
            ))}
          </select>
        </div>

        <button
          type="submit"
          disabled={saving}
          className="w-full py-2.5 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-60 disabled:cursor-not-allowed transition-colors"
        >
          {saving ? 'Saving...' : 'Save Profile'}
        </button>

        {apiError && (
          <div className="bg-red-50 border border-red-200 rounded-lg px-3 py-2">
            <p className="text-sm text-red-700 text-center">{apiError}</p>
          </div>
        )}
      </form>
    </div>
  );
};

export default ProfileSetupPage;
