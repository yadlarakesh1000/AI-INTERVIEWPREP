const DayCheckbox = ({ day, onToggle, disabled }) => {
  const completed = day.isCompleted;
  return (
    <label
      className={`flex items-center gap-3 px-4 py-3 border rounded-lg transition-colors ${
        disabled ? 'cursor-wait opacity-70' : 'cursor-pointer'
      } ${completed ? 'border-green-300 bg-green-50' : 'border-slate-200 hover:bg-slate-50'}`}
    >
      <input
        type="checkbox"
        checked={!!completed}
        onChange={() => onToggle(day.dayNumber)}
        disabled={disabled}
        className="w-4 h-4 accent-indigo-600 shrink-0"
      />
      <span className="text-xs font-medium text-slate-400 w-12 shrink-0">Day {day.dayNumber}</span>
      <span
        className={`text-sm flex-1 ${completed ? 'text-slate-500 line-through' : 'text-slate-700'}`}
      >
        {day.topic}
      </span>
    </label>
  );
};

export default DayCheckbox;
