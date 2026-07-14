import InterviewSetup from '../components/interview/InterviewSetup';

const InterviewSetupPage = () => {
  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800">Set Up Your Mock Interview</h1>
      <p className="mt-1 text-sm text-slate-500">
        Choose what you want to practice — the AI interviewer will ask questions by voice and
        evaluate every answer.
      </p>

      <div className="mt-6">
        <InterviewSetup />
      </div>
    </div>
  );
};

export default InterviewSetupPage;
