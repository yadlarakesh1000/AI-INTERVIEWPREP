// 4-dimension rubric display with Section 12 color coding:
// green 8-10, yellow 5-7, red 1-4.
const scoreClasses = (score) => {
  if (score >= 8) return 'bg-green-100 text-green-700';
  if (score >= 5) return 'bg-yellow-100 text-yellow-700';
  return 'bg-red-100 text-red-700';
};

const DIMENSIONS = [
  { key: 'relevance', label: 'Relevance' },
  { key: 'clarityAndStructure', label: 'Clarity & Structure' },
  { key: 'depthAndAccuracy', label: 'Depth / Accuracy' },
  { key: 'confidenceAndFluency', label: 'Confidence & Fluency' },
];

const EvaluationCard = ({ transcribedAnswer, evaluation }) => {
  if (!evaluation) return null;

  return (
    <div className="w-full max-w-2xl mx-auto flex flex-col gap-4">
      {/* What the AI "heard" (Section 12: answer transcript area) */}
      <div className="bg-white border border-slate-200 rounded-xl p-5">
        <h3 className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
          Your Answer (transcribed)
        </h3>
        <p className="mt-2 text-sm text-slate-700 italic">
          {transcribedAnswer?.trim()
            ? `“${transcribedAnswer.trim()}”`
            : 'No speech was detected in your recording.'}
        </p>
      </div>

      {/* 4-dimension rubric */}
      <div className="bg-white border border-slate-200 rounded-xl p-5">
        <div className="flex items-center justify-between">
          <h3 className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
            Evaluation
          </h3>
          <div className="flex gap-2 text-xs font-semibold">
            <span className={`px-2.5 py-1 rounded-full ${scoreClasses(evaluation.overallScore)}`}>
              Overall {evaluation.overallScore.toFixed(1)}
            </span>
            <span
              className={`px-2.5 py-1 rounded-full ${scoreClasses(evaluation.confidenceScore)}`}
            >
              Confidence {evaluation.confidenceScore.toFixed(1)}
            </span>
          </div>
        </div>

        <ul className="mt-4 flex flex-col gap-3">
          {DIMENSIONS.map(({ key, label }) => {
            const dim = evaluation[key];
            if (!dim) return null;
            return (
              <li key={key} className="flex items-start gap-3">
                <span
                  className={`shrink-0 w-9 h-9 rounded-lg flex items-center justify-center text-sm font-bold ${scoreClasses(dim.score)}`}
                >
                  {dim.score}
                </span>
                <div>
                  <p className="text-sm font-medium text-slate-800">{label}</p>
                  <p className="text-xs text-slate-500">{dim.feedback}</p>
                </div>
              </li>
            );
          })}
        </ul>
      </div>
    </div>
  );
};

export default EvaluationCard;
