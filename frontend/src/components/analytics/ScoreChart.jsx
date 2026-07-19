import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';

// Line chart of the last 5 interview scores (Section 12): two lines, overall
// and confidence, plotted over time.
const ScoreChart = ({ recentScores }) => {
  if (!recentScores || recentScores.length === 0) {
    return (
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
        <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
          Score Trend
        </h2>
        <p className="mt-3 text-sm text-slate-400">
          No completed interviews yet. Your score trend will appear here.
        </p>
      </div>
    );
  }

  // recentScores arrives oldest-first for the chart's left-to-right timeline.
  const data = recentScores.map((s) => ({
    date: s.date,
    Overall: s.overallScore,
    Confidence: s.confidenceScore,
  }));

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
      <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wide mb-4">
        Score Trend (last {recentScores.length})
      </h2>
      <div className="w-full h-72">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={data} margin={{ top: 5, right: 20, bottom: 5, left: -10 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
            <XAxis dataKey="date" tick={{ fontSize: 12, fill: '#64748b' }} />
            <YAxis domain={[0, 10]} tick={{ fontSize: 12, fill: '#64748b' }} />
            <Tooltip />
            <Legend />
            <Line
              type="monotone"
              dataKey="Overall"
              stroke="#4f46e5"
              strokeWidth={2}
              dot={{ r: 3 }}
            />
            <Line
              type="monotone"
              dataKey="Confidence"
              stroke="#10b981"
              strokeWidth={2}
              dot={{ r: 3 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default ScoreChart;
