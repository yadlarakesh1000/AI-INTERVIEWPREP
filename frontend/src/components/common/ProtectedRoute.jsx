import { Navigate, Outlet } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import Navbar from './Navbar';
import Sidebar from './Sidebar';

// Layout route for authenticated pages: bounces to /login when not
// authenticated, otherwise renders the app shell around the child route.
// Pass layout={false} for focused full-screen pages (the live interview)
// that must render without the navbar/sidebar per Section 12.
const ProtectedRoute = ({ layout = true }) => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!layout) {
    return <Outlet />;
  }

  return (
    <div className="min-h-screen bg-slate-100">
      <Navbar />
      <div className="flex">
        <Sidebar />
        <main className="flex-1 p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default ProtectedRoute;
