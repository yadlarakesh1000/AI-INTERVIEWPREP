import { useNavigate } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';

const Navbar = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="bg-white border-b border-slate-200 shadow-sm">
      <div className="flex items-center justify-between px-6 h-16">
        <div className="flex items-center gap-2">
          <span className="text-2xl">🎙️</span>
          <span className="text-xl font-bold text-slate-800">
            AI <span className="text-indigo-600">InterviewPrep</span>
          </span>
        </div>

        {isAuthenticated && (
          <div className="flex items-center gap-4">
            <span className="text-sm text-slate-600 hidden sm:block">
              {user?.email}
            </span>
            <button
              onClick={handleLogout}
              className="px-4 py-2 text-sm font-medium text-slate-600 border border-slate-300 rounded-lg hover:bg-slate-100 hover:text-slate-800 transition-colors"
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </header>
  );
};

export default Navbar;
