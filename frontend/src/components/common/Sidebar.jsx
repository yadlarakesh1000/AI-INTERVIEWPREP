import { NavLink } from 'react-router-dom';

const links = [
  { to: '/dashboard', label: 'Dashboard', icon: '🏠' },
  { to: '/interviews/setup', label: 'Mock Interview', icon: '🎤' },
  { to: '/roadmaps', label: 'Career Roadmaps', icon: '🗺️' },
  { to: '/skill-tracker', label: 'Skill Tracker', icon: '✅' },
  { to: '/recommendations', label: 'Recommendations', icon: '💡' },
  { to: '/analytics', label: 'Analytics', icon: '📊' },
  { to: '/history', label: 'History', icon: '🕘' },
];

const Sidebar = () => {
  return (
    <aside className="w-56 shrink-0 bg-white border-r border-slate-200 min-h-[calc(100vh-4rem)] p-4">
      <nav className="flex flex-col gap-1">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-indigo-50 text-indigo-700'
                  : 'text-slate-600 hover:bg-slate-100 hover:text-slate-800'
              }`
            }
          >
            <span>{link.icon}</span>
            <span>{link.label}</span>
          </NavLink>
        ))}
      </nav>
    </aside>
  );
};

export default Sidebar;
