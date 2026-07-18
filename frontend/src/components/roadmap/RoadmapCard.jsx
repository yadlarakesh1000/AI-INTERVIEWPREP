import { useNavigate } from 'react-router-dom';

const RoadmapCard = ({ roadmap, isActive }) => {
  const navigate = useNavigate();

  return (
    <button
      type="button"
      onClick={() => navigate(`/roadmaps/${roadmap.id}`)}
      className="text-left bg-white rounded-xl border border-slate-200 shadow-sm p-6 transition-all hover:shadow-md hover:-translate-y-0.5 hover:border-indigo-300 flex flex-col"
    >
      <div className="flex items-start justify-between gap-2">
        <span className="text-2xl">🗺️</span>
        {isActive && (
          <span className="px-2 py-0.5 text-xs font-semibold text-green-700 bg-green-100 rounded-full">
            Active
          </span>
        )}
      </div>
      <h2 className="mt-3 text-base font-semibold text-slate-800">{roadmap.title}</h2>
      <p className="mt-1 text-xs font-medium text-indigo-600">{roadmap.careerPath}</p>
      <p className="mt-2 text-sm text-slate-500 line-clamp-3 flex-1">{roadmap.description}</p>
      <p className="mt-4 text-xs font-medium text-slate-400">{roadmap.durationWeeks} weeks</p>
    </button>
  );
};

export default RoadmapCard;
