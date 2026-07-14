import { useEffect, useState } from 'react';

const formatTime = (totalSeconds) => {
  const m = String(Math.floor(totalSeconds / 60)).padStart(2, '0');
  const s = String(totalSeconds % 60).padStart(2, '0');
  return `${m}:${s}`;
};

// Large central record button (Section 12): tap to start recording, tap
// again to stop. Shows a pulsing indicator and timer while recording.
const MicrophoneButton = ({ isRecording, disabled, onClick, hint }) => {
  const [seconds, setSeconds] = useState(0);

  useEffect(() => {
    if (!isRecording) {
      setSeconds(0);
      return undefined;
    }
    const interval = setInterval(() => setSeconds((s) => s + 1), 1000);
    return () => clearInterval(interval);
  }, [isRecording]);

  return (
    <div className="flex flex-col items-center gap-3">
      <button
        type="button"
        onClick={onClick}
        disabled={disabled}
        aria-label={isRecording ? 'Stop recording' : 'Start recording'}
        className={`relative w-20 h-20 rounded-full flex items-center justify-center text-3xl shadow-lg transition-all focus:outline-none focus:ring-4 ${
          isRecording
            ? 'bg-red-500 hover:bg-red-600 focus:ring-red-200'
            : 'bg-indigo-600 hover:bg-indigo-700 focus:ring-indigo-200'
        } disabled:opacity-40 disabled:cursor-not-allowed`}
      >
        {isRecording && (
          <span className="absolute inset-0 rounded-full bg-red-400 animate-ping opacity-40" />
        )}
        <span className="relative">{isRecording ? '⏹️' : '🎙️'}</span>
      </button>

      {isRecording ? (
        <div className="flex items-center gap-2 text-sm font-medium text-red-600">
          <span className="w-2 h-2 bg-red-500 rounded-full animate-pulse" />
          Listening... {formatTime(seconds)}
        </div>
      ) : (
        <p className="text-sm text-slate-500">{hint}</p>
      )}
    </div>
  );
};

export default MicrophoneButton;
