// Shows the current question prominently while the AI asks it (Section 12).
const QuestionDisplay = ({ questionText, questionNumber, totalQuestions, isSpeaking }) => {
  return (
    <div className="text-center">
      <p className="text-xs font-semibold text-indigo-600 uppercase tracking-widest">
        Question {questionNumber} of {totalQuestions}
      </p>
      <h2 className="mt-3 text-xl sm:text-2xl font-semibold text-slate-800 leading-snug max-w-2xl mx-auto">
        {questionText}
      </h2>
      {isSpeaking && (
        <p className="mt-3 inline-flex items-center gap-2 text-sm text-slate-500">
          <span className="flex gap-1">
            <span className="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce [animation-delay:0ms]" />
            <span className="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce [animation-delay:150ms]" />
            <span className="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce [animation-delay:300ms]" />
          </span>
          AI is speaking...
        </p>
      )}
    </div>
  );
};

export default QuestionDisplay;
