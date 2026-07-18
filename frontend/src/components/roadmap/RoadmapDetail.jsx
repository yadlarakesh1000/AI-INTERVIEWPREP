import { useState } from 'react';
import { Link } from 'react-router-dom';

// One collapsible week in the weekly plan (Section 12: accordion / scrollable list).
const WeekAccordion = ({ week }) => {
  const [open, setOpen] = useState(false);
  return (
    <div className="border border-slate-200 rounded-lg overflow-hidden">
      <button
        type="button"
        onClick={() => setOpen((o) => !o)}
        className="w-full flex items-center justify-between px-4 py-3 text-left hover:bg-slate-50 transition-colors"
      >
        <span className="text-sm font-medium text-slate-800">
          Week {week.week}: {week.title}
        </span>
        <span className="text-slate-400 text-xs">{open ? '▲' : '▼'}</span>
      </button>
      {open && (
        <ul className="px-4 pb-3 flex flex-col gap-1 border-t border-slate-100 pt-2">
          {week.days?.map((day) => (
            <li key={day.day} className="text-sm text-slate-600 flex gap-2">
              <span className="text-slate-400 w-12 shrink-0">Day {day.day}</span>
              <span>{day.topic}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

const RoadmapDetail = ({ roadmap, isActive, onSelect, isSelecting, selectError }) => {
  return (
    <div className="max-w-3xl mx-auto flex flex-col gap-6">
      <div>
        <Link to="/roadmaps" className="text-sm text-indigo-600 hover:text-indigo-700">
          ← Back to roadmaps
        </Link>
        <div className="mt-3 flex items-start justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-slate-800">{roadmap.title}</h1>
            <p className="mt-1 text-sm font-medium text-indigo-600">{roadmap.careerPath}</p>
          </div>
          {isActive && (
            <span className="px-3 py-1 text-xs font-semibold text-green-700 bg-green-100 rounded-full shrink-0">
              Currently Active
            </span>
          )}
        </div>
        <p className="mt-3 text-sm text-slate-600">{roadmap.description}</p>
        <p className="mt-2 text-xs font-medium text-slate-400">
          {roadmap.durationWeeks} weeks
        </p>
      </div>

      {/* Milestones */}
      {roadmap.milestones?.length > 0 && (
        <section className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
          <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
            Milestones
          </h2>
          <ul className="mt-3 flex flex-col gap-3">
            {roadmap.milestones.map((m, i) => (
              <li key={i} className="flex gap-3">
                <span className="shrink-0 w-16 text-xs font-semibold text-indigo-600 pt-0.5">
                  Week {m.week}
                </span>
                <div>
                  <p className="text-sm font-medium text-slate-800">{m.title}</p>
                  {m.description && <p className="text-xs text-slate-500">{m.description}</p>}
                </div>
              </li>
            ))}
          </ul>
        </section>
      )}

      {/* Weekly plan */}
      {roadmap.weeklyPlan?.length > 0 && (
        <section className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
          <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
            Weekly Plan
          </h2>
          <div className="mt-3 flex flex-col gap-2">
            {roadmap.weeklyPlan.map((week) => (
              <WeekAccordion key={week.week} week={week} />
            ))}
          </div>
        </section>
      )}

      {/* Recommended projects */}
      {roadmap.recommendedProjects?.length > 0 && (
        <section className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
          <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
            Recommended Projects
          </h2>
          <ul className="mt-3 flex flex-col gap-3">
            {roadmap.recommendedProjects.map((p, i) => (
              <li key={i} className="border border-slate-200 rounded-lg p-4">
                <p className="text-sm font-medium text-slate-800">{p.name}</p>
                {p.description && <p className="mt-1 text-xs text-slate-500">{p.description}</p>}
              </li>
            ))}
          </ul>
        </section>
      )}

      {/* Learning resources */}
      {roadmap.learningResources?.length > 0 && (
        <section className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
          <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
            Learning Resources
          </h2>
          <ul className="mt-3 flex flex-col gap-2">
            {roadmap.learningResources.map((r, i) => (
              <li key={i} className="text-sm">
                {r.url ? (
                  <a
                    href={r.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-indigo-600 hover:text-indigo-700"
                  >
                    {r.name || r.url}
                  </a>
                ) : (
                  <span className="text-slate-600">{r.name}</span>
                )}
              </li>
            ))}
          </ul>
        </section>
      )}

      {/* Select action */}
      <div className="flex flex-col gap-2">
        {isActive ? (
          <Link
            to="/skill-tracker"
            className="w-full sm:w-auto text-center px-8 py-3 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition-colors"
          >
            Go to Skill Tracker →
          </Link>
        ) : (
          <button
            type="button"
            onClick={onSelect}
            disabled={isSelecting}
            className="w-full sm:w-auto px-8 py-3 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-60 disabled:cursor-not-allowed transition-colors"
          >
            {isSelecting ? 'Selecting...' : 'Select This Roadmap'}
          </button>
        )}
        {selectError && <p className="text-sm text-red-600">{selectError}</p>}
      </div>
    </div>
  );
};

export default RoadmapDetail;
