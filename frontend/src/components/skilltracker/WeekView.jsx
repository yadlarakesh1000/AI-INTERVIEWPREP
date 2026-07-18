import DayCheckbox from './DayCheckbox';

// A progress bar used for both per-week and overall progress.
const ProgressBar = ({ percent }) => {
  const value = Math.round(percent || 0);
  return (
    <div className="w-full">
      <div className="h-2 bg-slate-200 rounded-full overflow-hidden">
        <div
          className="h-full bg-indigo-600 rounded-full transition-all"
          style={{ width: `${Math.min(100, Math.max(0, value))}%` }}
        />
      </div>
    </div>
  );
};

const WeekView = ({
  week,
  totalWeeks,
  onPrev,
  onNext,
  onToggleDay,
  togglingDay,
}) => {
  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
      {/* Week navigator */}
      <div className="flex items-center justify-between">
        <button
          type="button"
          onClick={onPrev}
          disabled={week.weekNumber <= 1}
          className="px-3 py-1.5 text-sm font-medium text-slate-600 border border-slate-300 rounded-lg hover:bg-slate-100 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
        >
          ← Prev
        </button>
        <div className="text-center">
          <p className="text-xs font-medium text-slate-400">
            Week {week.weekNumber} of {totalWeeks}
          </p>
          <h2 className="text-base font-semibold text-slate-800">{week.weekTitle}</h2>
        </div>
        <button
          type="button"
          onClick={onNext}
          disabled={week.weekNumber >= totalWeeks}
          className="px-3 py-1.5 text-sm font-medium text-slate-600 border border-slate-300 rounded-lg hover:bg-slate-100 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
        >
          Next →
        </button>
      </div>

      {/* Per-week progress */}
      <div className="mt-4 flex items-center gap-3">
        <ProgressBar percent={week.progressPercent} />
        <span className="text-xs font-medium text-slate-500 shrink-0 w-10 text-right">
          {Math.round(week.progressPercent || 0)}%
        </span>
      </div>

      {/* 7-day checklist */}
      <div className="mt-5 flex flex-col gap-2">
        {week.days?.map((day) => (
          <DayCheckbox
            key={day.dayNumber}
            day={day}
            onToggle={onToggleDay}
            disabled={togglingDay === day.dayNumber}
          />
        ))}
      </div>
    </div>
  );
};

export { ProgressBar };
export default WeekView;
