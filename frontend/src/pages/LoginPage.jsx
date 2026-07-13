import { Link, Navigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import LoginForm from '../components/auth/LoginForm';

const LoginPage = () => {
  const { isAuthenticated } = useAuth();

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-6">
          <div className="text-4xl mb-2">🎙️</div>
          <h1 className="text-2xl font-bold text-slate-800">
            AI <span className="text-indigo-600">InterviewPrep</span>
          </h1>
          <p className="mt-1 text-sm text-slate-500">Sign in to continue your interview practice</p>
        </div>

        <div className="bg-white rounded-xl shadow-md p-8">
          <LoginForm />
        </div>

        <p className="mt-4 text-center text-sm text-slate-500">
          Don&apos;t have an account?{' '}
          <Link to="/register" className="font-medium text-indigo-600 hover:text-indigo-700">
            Create one
          </Link>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
