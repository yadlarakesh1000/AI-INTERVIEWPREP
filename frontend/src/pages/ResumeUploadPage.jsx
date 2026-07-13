import ResumeUpload from '../components/resume/ResumeUpload';

const ResumeUploadPage = () => {
  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800">Upload Your Resume</h1>
      <p className="mt-1 text-sm text-slate-500">
        We&apos;ll extract your skills, projects, and experience to generate personalized
        resume-based interview questions.
      </p>

      <div className="mt-6">
        <ResumeUpload />
      </div>
    </div>
  );
};

export default ResumeUploadPage;
