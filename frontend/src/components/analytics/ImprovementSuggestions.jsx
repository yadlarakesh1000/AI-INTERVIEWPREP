const ImprovementSuggestions = ({ suggestions }) => {
  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
      <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
        Latest Improvement Suggestions
      </h2>
      {suggestions?.length > 0 ? (
        <ul className="mt-3 flex flex-col gap-2">
          {suggestions.map((suggestion, i) => (
            <li key={i} className="flex items-start gap-2 text-sm text-slate-600">
              <span className="text-indigo-500 mt-0.5">•</span>
              {suggestion}
            </li>
          ))}
        </ul>
      ) : (
        <p className="mt-3 text-sm text-slate-400">
          Complete an interview to get personalized suggestions.
        </p>
      )}
    </div>
  );
};

export default ImprovementSuggestions;
