import { useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useApi from '../../hooks/useApi';

const MAX_SIZE_BYTES = 5 * 1024 * 1024;

const formatSize = (bytes) => {
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(0)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
};

// parsedData comes back from the backend as a raw JSON string.
const parseResumeData = (resume) => {
  if (!resume?.parsedData) return null;
  try {
    return JSON.parse(resume.parsedData);
  } catch {
    return null;
  }
};

const ResumeUpload = () => {
  const { post } = useApi();
  const navigate = useNavigate();
  const inputRef = useRef(null);

  const [file, setFile] = useState(null);
  const [error, setError] = useState('');
  const [uploading, setUploading] = useState(false);
  const [dragOver, setDragOver] = useState(false);
  const [uploaded, setUploaded] = useState(null);

  const validateAndSelect = (candidate) => {
    setError('');
    if (!candidate) return;
    const isPdf =
      candidate.type === 'application/pdf' || candidate.name.toLowerCase().endsWith('.pdf');
    if (!isPdf) {
      setError('Only PDF files are accepted.');
      return;
    }
    if (candidate.size > MAX_SIZE_BYTES) {
      setError(`File is too large (${formatSize(candidate.size)}). Maximum size is 5 MB.`);
      return;
    }
    setFile(candidate);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    validateAndSelect(e.dataTransfer.files?.[0]);
  };

  const handleUpload = async () => {
    if (!file) return;
    setUploading(true);
    setError('');
    try {
      const formData = new FormData();
      formData.append('resume', file);
      const response = await post('/resumes/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      setUploaded(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setUploading(false);
    }
  };

  const resetForNewUpload = () => {
    setUploaded(null);
    setFile(null);
    setError('');
  };

  // ---------- After upload: parsed data preview ----------
  if (uploaded) {
    const parsed = parseResumeData(uploaded);
    return (
      <div className="bg-white rounded-xl shadow-md p-8">
        <div className="flex items-center gap-3">
          <span className="text-3xl">✅</span>
          <div>
            <h2 className="text-lg font-semibold text-slate-800">Resume uploaded and parsed</h2>
            <p className="text-sm text-slate-500">{uploaded.fileName}</p>
          </div>
        </div>

        {parsed ? (
          <div className="mt-6 flex flex-col gap-6">
            {parsed.skills?.length > 0 && (
              <div>
                <h3 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
                  Skills
                </h3>
                <div className="mt-2 flex flex-wrap gap-2">
                  {parsed.skills.map((skill) => (
                    <span
                      key={skill}
                      className="px-3 py-1 bg-indigo-50 text-indigo-700 text-sm rounded-full"
                    >
                      {skill}
                    </span>
                  ))}
                </div>
              </div>
            )}

            {parsed.projects?.length > 0 && (
              <div>
                <h3 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
                  Projects
                </h3>
                <ul className="mt-2 flex flex-col gap-3">
                  {parsed.projects.map((project, i) => (
                    <li key={i} className="border border-slate-200 rounded-lg p-4">
                      <p className="text-sm font-semibold text-slate-800">{project.name}</p>
                      <p className="mt-1 text-sm text-slate-500">{project.description}</p>
                      {project.technologies?.length > 0 && (
                        <p className="mt-1 text-xs text-slate-400">
                          {project.technologies.join(' · ')}
                        </p>
                      )}
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {parsed.experience?.length > 0 && (
              <div>
                <h3 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
                  Experience
                </h3>
                <ul className="mt-2 flex flex-col gap-3">
                  {parsed.experience.map((exp, i) => (
                    <li key={i} className="border border-slate-200 rounded-lg p-4">
                      <p className="text-sm font-semibold text-slate-800">
                        {exp.role}
                        {exp.company ? ` — ${exp.company}` : ''}
                      </p>
                      {exp.duration && <p className="text-xs text-slate-400">{exp.duration}</p>}
                      {exp.description && (
                        <p className="mt-1 text-sm text-slate-500">{exp.description}</p>
                      )}
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {parsed.education && (
              <div>
                <h3 className="text-sm font-semibold text-slate-700 uppercase tracking-wide">
                  Education
                </h3>
                <p className="mt-2 text-sm text-slate-600">{parsed.education}</p>
              </div>
            )}
          </div>
        ) : (
          <p className="mt-6 text-sm text-slate-500">
            The resume text was extracted, but structured parsing is unavailable right now. You can
            still use it for a resume-based interview.
          </p>
        )}

        <div className="mt-8 flex flex-col sm:flex-row gap-3">
          <button
            onClick={() => navigate('/interviews/setup')}
            className="flex-1 py-2.5 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition-colors"
          >
            Continue to Interview
          </button>
          <button
            onClick={resetForNewUpload}
            className="flex-1 py-2.5 text-sm font-medium text-slate-600 border border-slate-300 rounded-lg hover:bg-slate-100 transition-colors"
          >
            Upload a Different Resume
          </button>
        </div>
      </div>
    );
  }

  // ---------- Before upload: drop zone + picker ----------
  return (
    <div className="bg-white rounded-xl shadow-md p-8">
      <div
        onDragOver={(e) => {
          e.preventDefault();
          setDragOver(true);
        }}
        onDragLeave={() => setDragOver(false)}
        onDrop={handleDrop}
        onClick={() => inputRef.current?.click()}
        className={`border-2 border-dashed rounded-xl p-10 text-center cursor-pointer transition-colors ${
          dragOver ? 'border-indigo-500 bg-indigo-50' : 'border-slate-300 hover:border-indigo-400'
        }`}
      >
        <div className="text-4xl">📄</div>
        <p className="mt-3 text-sm font-medium text-slate-700">
          {file ? file.name : 'Drag & drop your resume here, or click to browse'}
        </p>
        <p className="mt-1 text-xs text-slate-400">
          {file ? formatSize(file.size) : 'PDF only, up to 5 MB'}
        </p>
        <input
          ref={inputRef}
          type="file"
          accept="application/pdf,.pdf"
          className="hidden"
          onChange={(e) => validateAndSelect(e.target.files?.[0])}
        />
      </div>

      {error && (
        <div className="mt-4 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
          <p className="text-sm text-red-700 text-center">{error}</p>
        </div>
      )}

      <button
        onClick={handleUpload}
        disabled={!file || uploading}
        className="mt-6 w-full py-2.5 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-60 disabled:cursor-not-allowed transition-colors"
      >
        {uploading ? 'Uploading and parsing your resume...' : 'Upload Resume'}
      </button>
      {uploading && (
        <p className="mt-2 text-xs text-center text-slate-400">
          The AI is extracting your skills, projects, and experience — this can take a few seconds.
        </p>
      )}
    </div>
  );
};

export default ResumeUpload;
