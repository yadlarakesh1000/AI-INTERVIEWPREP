const ScoreCard = ({ label, value, suffix }) => {
  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 text-center">
      <p className="text-xs font-semibold text-slate-500 uppercase tracking-wide">{label}</p>
      <p className="mt-2 text-4xl font-bold text-slate-800">
        {value}
        {suffix && <span className="text-lg text-slate-400">{suffix}</span>}
      </p>
    </div>
  );
};

export default ScoreCard;
