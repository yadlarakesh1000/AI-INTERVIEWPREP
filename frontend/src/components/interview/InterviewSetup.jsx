import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import useApi from '../../hooks/useApi';

const TYPES = [
  {
    value: 'HR',
    icon: '🗣️',
    title: 'HR & Communication',
    description: 'Behavioral, situational, and motivation questions to sharpen how you present yourself.',
  },
  {
    value: 'RESUME_BASED',
    icon: '📄',
    title: 'Resume-Based',
    description: 'Personalized questions about the projects, skills, and experience on your resume.',
  },
  {
    value: 'CS_FUNDAMENTALS',
    icon: '💻',
    title: 'CS Fundamentals',
    description: 'Core computer science concepts: OOP, DBMS, Operating Systems, and Networks.',
  },
];

const DIFFICULTIES = [
  {
    value: 'EASY',
    title: 'Easy',
    description: 'Independent questions — a friendly warm-up at a steady pace.',
  },
  {
    value: 'MEDIUM',
    title: 'Medium',
    description: 'The AI asks follow-ups that dig deeper into your previous answer, like a real interviewer.',
  },
];

const CS_TOPICS = ['All', 'OOP', 'DBMS', 'Operating Systems', 'Computer Networks'];

const InterviewSetup = () => {
  const { get, post } = useApi();
  const navigate = useNavigate();

  const [interviewType, setInterviewType] = useState('HR');
  const [difficulty, setDifficulty] = useState('EASY');
  const [totalQuestions, setTotalQuestions] = useState(5);
  const [csTopic, setCsTopic] = useState('All');
  const [starting, setStarting] = useState(false);
  const [apiError, setApiError] = useState('');
  // Synchronous guard: React state updates too late to stop a double-click
  // from firing two POST /interviews/start calls.
  const startInFlight = useRef(false);

  const isResumeBased = interviewType === 'RESUME_BASED';

  const { data: resumeRes, isError: noResume, isLoading: resumeLoading } = useQuery({
    queryKey: ['latestResume'],
    queryFn: () => get('/resumes/latest'),
    retry: false,
    enabled: isResumeBased,
  });

  // Section 12: if Resume-Based is chosen but no resume exists, go upload one first.
  useEffect(() => {
    if (isResumeBased && noResume) {
      navigate('/resume/upload');
    }
  }, [isResumeBased, noResume, navigate]);

  const handleStart = async () => {
    if (startInFlight.current) return;
    startInFlight.current = true;
    setStarting(true);
    setApiError('');
    try {
      const response = await post('/interviews/start', {
        interviewType,
        difficulty,
        totalQuestions,
        csTopic: interviewType === 'CS_FUNDAMENTALS' && csTopic !== 'All' ? csTopic : null,
      });
      // Hand the full start payload (first question, TTS URL, session info)
      // to the InterviewPage via router state.
      navigate(`/interviews/${response.data.interviewId}`, { state: response.data });
    } catch (err) {
      setApiError(err.message);
      setStarting(false);
      startInFlight.current = false;
    }
  };

  return (
    <div className="flex flex-col gap-8">
      {/* Interview type */}
      <section>
        <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
          Interview Type
        </h2>
        <div className="mt-3 grid grid-cols-1 sm:grid-cols-3 gap-4">
          {TYPES.map((type) => (
            <button
              key={type.value}
              type="button"
              onClick={() => setInterviewType(type.value)}
              className={`text-left rounded-xl border p-5 transition-all ${
                interviewType === type.value
                  ? 'border-indigo-500 bg-indigo-50 ring-2 ring-indigo-200'
                  : 'border-slate-200 bg-white hover:border-indigo-300'
              }`}
            >
              <div className="text-2xl">{type.icon}</div>
              <p className="mt-2 text-sm font-semibold text-slate-800">{type.title}</p>
              <p className="mt-1 text-xs text-slate-500">{type.description}</p>
            </button>
          ))}
        </div>

        {isResumeBased && (
          <div className="mt-3 bg-white border border-slate-200 rounded-lg px-4 py-3 text-sm">
            {resumeLoading ? (
              <span className="text-slate-500">Checking your resume...</span>
            ) : resumeRes?.data ? (
              <span className="text-slate-600">
                Using resume: <span className="font-medium">{resumeRes.data.fileName}</span>{' '}
                <Link to="/resume/upload" className="ml-2 text-indigo-600 hover:text-indigo-700">
                  Upload a new one
                </Link>
              </span>
            ) : (
              <span className="text-slate-500">Redirecting to resume upload...</span>
            )}
          </div>
        )}
      </section>

      {/* Difficulty */}
      <section>
        <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">Difficulty</h2>
        <div className="mt-3 grid grid-cols-1 sm:grid-cols-2 gap-4">
          {DIFFICULTIES.map((level) => (
            <button
              key={level.value}
              type="button"
              onClick={() => setDifficulty(level.value)}
              className={`text-left rounded-xl border p-5 transition-all ${
                difficulty === level.value
                  ? 'border-indigo-500 bg-indigo-50 ring-2 ring-indigo-200'
                  : 'border-slate-200 bg-white hover:border-indigo-300'
              }`}
            >
              <p className="text-sm font-semibold text-slate-800">{level.title}</p>
              <p className="mt-1 text-xs text-slate-500">{level.description}</p>
            </button>
          ))}
        </div>
      </section>

      {/* CS topic — only for CS Fundamentals */}
      {interviewType === 'CS_FUNDAMENTALS' && (
        <section>
          <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">Topic</h2>
          <div className="mt-3 flex flex-wrap gap-2">
            {CS_TOPICS.map((topic) => (
              <button
                key={topic}
                type="button"
                onClick={() => setCsTopic(topic)}
                className={`px-4 py-2 rounded-full text-sm font-medium border transition-colors ${
                  csTopic === topic
                    ? 'border-indigo-500 bg-indigo-600 text-white'
                    : 'border-slate-300 bg-white text-slate-600 hover:border-indigo-400'
                }`}
              >
                {topic}
              </button>
            ))}
          </div>
        </section>
      )}

      {/* Question count */}
      <section>
        <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
          Number of Questions
        </h2>
        <div className="mt-3 bg-white border border-slate-200 rounded-xl p-5">
          <div className="flex items-center gap-4">
            <input
              type="range"
              min={3}
              max={10}
              value={totalQuestions}
              onChange={(e) => setTotalQuestions(Number(e.target.value))}
              className="flex-1 accent-indigo-600"
            />
            <span className="w-24 text-sm font-semibold text-slate-800 text-right">
              {totalQuestions} questions
            </span>
          </div>
          <p className="mt-2 text-xs text-slate-400">
            3–10 questions. Around {totalQuestions * 2} minutes of practice.
          </p>
        </div>
      </section>

      <div>
        <button
          type="button"
          onClick={handleStart}
          disabled={starting || (isResumeBased && !resumeRes?.data)}
          className="w-full sm:w-auto px-8 py-3 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-60 disabled:cursor-not-allowed transition-colors"
        >
          {starting ? 'Preparing your first question...' : 'Start Interview'}
        </button>
        {starting && (
          <p className="mt-2 text-xs text-slate-400">
            The AI interviewer is getting ready — this takes a few seconds.
          </p>
        )}
        {apiError && (
          <div className="mt-3 bg-red-50 border border-red-200 rounded-lg px-3 py-2 inline-block">
            <p className="text-sm text-red-700">{apiError}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default InterviewSetup;
