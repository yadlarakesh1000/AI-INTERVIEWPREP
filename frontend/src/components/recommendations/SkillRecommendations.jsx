const SkillRecommendations = ({ data }) => {
  return (
    <div className="flex flex-col gap-6">
      {/* Current skills as tags */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
        <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
          Your Current Skills
        </h2>
        {data.careerPath && (
          <p className="mt-1 text-xs text-slate-500">
            Career path: <span className="font-medium text-indigo-600">{data.careerPath}</span>
          </p>
        )}
        <div className="mt-3 flex flex-wrap gap-2">
          {data.currentSkills?.length > 0 ? (
            data.currentSkills.map((skill) => (
              <span
                key={skill}
                className="px-3 py-1 bg-slate-100 text-slate-700 text-sm rounded-full"
              >
                {skill}
              </span>
            ))
          ) : (
            <span className="text-sm text-slate-400">No skills on your profile yet.</span>
          )}
        </div>
      </div>

      {/* Recommended skills as cards */}
      <div>
        <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
          Recommended Next Skills
        </h2>
        {data.recommendations?.length > 0 ? (
          <div className="mt-3 grid grid-cols-1 sm:grid-cols-2 gap-4">
            {data.recommendations.map((rec, i) => (
              <div
                key={`${rec.skill}-${i}`}
                className="bg-white rounded-xl border border-slate-200 shadow-sm p-5"
              >
                <div className="flex items-center gap-2">
                  <span className="text-lg">💡</span>
                  <h3 className="text-base font-semibold text-slate-800">{rec.skill}</h3>
                </div>
                <p className="mt-2 text-sm text-slate-500">{rec.reason}</p>
              </div>
            ))}
          </div>
        ) : (
          <p className="mt-3 text-sm text-slate-400">No recommendations available right now.</p>
        )}
      </div>
    </div>
  );
};

export default SkillRecommendations;
