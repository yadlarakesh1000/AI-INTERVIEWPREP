import RoadmapCard from './RoadmapCard';

const RoadmapList = ({ roadmaps, activeRoadmapId }) => {
  if (!roadmaps || roadmaps.length === 0) {
    return (
      <div className="text-center py-16 text-slate-500">
        No roadmaps are available right now.
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      {roadmaps.map((roadmap) => (
        <RoadmapCard
          key={roadmap.id}
          roadmap={roadmap}
          isActive={roadmap.id === activeRoadmapId}
        />
      ))}
    </div>
  );
};

export default RoadmapList;
