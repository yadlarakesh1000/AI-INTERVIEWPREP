import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useApi from '../../hooks/useApi';
import useVoice from '../../hooks/useVoice';
import QuestionDisplay from './QuestionDisplay';
import MicrophoneButton from './MicrophoneButton';
import EvaluationCard from './EvaluationCard';

// Interview phases (Section 12/14 voice cycle):
// speaking -> ready -> recording -> evaluating -> evaluated -> (next) speaking...
const InterviewScreen = ({ startData }) => {
  const { get, post } = useApi();
  const navigate = useNavigate();
  const { startRecording, stopRecording, resetRecording, audioBlob, isRecording, error: micError } =
    useVoice();

  const [phase, setPhase] = useState('speaking');
  const [question, setQuestion] = useState({
    text: startData.question.text,
    number: startData.question.questionNumber,
    ttsAudioUrl: startData.ttsAudioUrl,
  });
  const [result, setResult] = useState(null); // last answer's transcript + evaluation
  const [nextQuestion, setNextQuestion] = useState(null);
  const [isLast, setIsLast] = useState(false);
  const [apiError, setApiError] = useState('');
  const [ending, setEnding] = useState(false);

  const audioRef = useRef(null);
  const fallbackTimerRef = useRef(null);
  const submitInFlight = useRef(false);

  const cleanupAudio = useCallback(() => {
    if (audioRef.current) {
      audioRef.current.pause();
      if (audioRef.current.src?.startsWith('blob:')) {
        URL.revokeObjectURL(audioRef.current.src);
      }
      audioRef.current = null;
    }
    if (window.speechSynthesis) window.speechSynthesis.cancel();
    if (fallbackTimerRef.current) clearTimeout(fallbackTimerRef.current);
  }, []);

  // Section 13 fallback: if Gemini TTS audio is unavailable (rate-limited,
  // no API key, or the audio endpoint 404s), use the browser's built-in
  // Web Speech API so the interview still speaks. A duration-based guard
  // timer covers browsers where utterance end events never fire.
  const speakWithBrowserFallback = useCallback((text) => {
    const finish = () => {
      if (fallbackTimerRef.current) clearTimeout(fallbackTimerRef.current);
      setPhase((p) => (p === 'speaking' ? 'ready' : p));
    };
    if (!window.speechSynthesis) {
      finish();
      return;
    }
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.onend = finish;
    utterance.onerror = finish;
    window.speechSynthesis.speak(utterance);
    const estimatedMs = text.split(/\s+/).length * 450 + 2000;
    fallbackTimerRef.current = setTimeout(finish, estimatedMs);
  }, []);

  const speakQuestion = useCallback(
    async (q) => {
      setPhase('speaking');
      cleanupAudio();
      try {
        // ttsAudioUrl is "/api/interviews/audio/..." — the axios baseURL
        // already ends in /api, so strip that prefix before requesting.
        const path = q.ttsAudioUrl?.replace(/^\/api/, '');
        if (!path) throw new Error('No TTS audio for this question');
        const response = await get(path, { responseType: 'blob' });
        if (!response || !(response instanceof Blob) || response.size === 0) {
          throw new Error('Empty TTS audio');
        }
        const audio = new Audio(URL.createObjectURL(response));
        audioRef.current = audio;
        audio.onended = () => setPhase((p) => (p === 'speaking' ? 'ready' : p));
        audio.onerror = () => speakWithBrowserFallback(q.text);
        await audio.play();
      } catch {
        // Backend TTS unavailable — fall back to the browser voice (or, if
        // even that fails, just show the text and enable the microphone).
        speakWithBrowserFallback(q.text);
      }
    },
    [get, cleanupAudio, speakWithBrowserFallback]
  );

  // Speak whenever a new question becomes current (including the first).
  useEffect(() => {
    speakQuestion(question);
    return cleanupAudio;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [question]);

  // Blob responses bypass useApi's JSON envelope: raw axios get returns the
  // ApiResponse-shaped body only for JSON. For blobs, response.data IS the blob.
  // (useApi.get returns response.data either way, so this works for both.)

  const handleMicClick = () => {
    setApiError('');
    if (phase === 'ready') {
      setPhase('recording');
      startRecording();
    } else if (phase === 'recording') {
      stopRecording();
      setPhase('evaluating');
    }
  };

  // Mic permission errors: go back to ready so the user can retry.
  useEffect(() => {
    if (micError && phase === 'recording') setPhase('ready');
  }, [micError, phase]);

  // When the recording blob is ready, submit it for transcription + evaluation.
  useEffect(() => {
    const submit = async () => {
      if (!audioBlob || phase !== 'evaluating' || submitInFlight.current) return;
      submitInFlight.current = true;
      try {
        const formData = new FormData();
        formData.append('audio', audioBlob, 'answer.webm');
        const response = await post(`/interviews/${startData.interviewId}/answer`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });
        const data = response.data;
        setResult({
          transcribedAnswer: data.transcribedAnswer,
          evaluation: data.evaluation,
        });
        setNextQuestion(
          data.nextQuestion
            ? {
                text: data.nextQuestion.text,
                number: data.nextQuestion.questionNumber,
                ttsAudioUrl: data.ttsAudioUrl,
              }
            : null
        );
        setIsLast(data.isLastQuestion || !data.nextQuestion);
        setPhase('evaluated');
      } catch (err) {
        setApiError(err.message);
        setPhase('ready');
      } finally {
        submitInFlight.current = false;
        resetRecording();
      }
    };
    submit();
  }, [audioBlob, phase, post, resetRecording, startData.interviewId]);

  const handleNextQuestion = () => {
    if (!nextQuestion) return;
    setResult(null);
    setApiError('');
    setQuestion(nextQuestion);
    setNextQuestion(null);
  };

  const handleEndInterview = async () => {
    if (ending) return;
    setEnding(true);
    setApiError('');
    cleanupAudio();
    try {
      const response = await post(`/interviews/${startData.interviewId}/end`);
      navigate(`/interviews/${startData.interviewId}/result`, { state: response.data });
    } catch (err) {
      setApiError(err.message);
      setEnding(false);
    }
  };

  const micHint =
    phase === 'ready'
      ? 'Tap to record your answer'
      : phase === 'speaking'
        ? 'Listen to the question...'
        : phase === 'evaluating'
          ? 'Processing...'
          : '';

  return (
    <div className="min-h-screen bg-slate-100 flex flex-col">
      {/* Minimal header — deliberately no navbar/sidebar (Section 12: focused layout) */}
      <header className="bg-white border-b border-slate-200 px-6 h-14 flex items-center justify-between">
        <span className="text-sm font-semibold text-slate-700">
          🎙️ Mock Interview — {startData.interviewType.replace('_', ' ')} ·{' '}
          {startData.difficulty}
        </span>
        <span className="text-xs text-slate-400">
          Question {question.number} of {startData.totalQuestions}
        </span>
      </header>

      <main className="flex-1 flex flex-col items-center justify-center gap-8 px-4 py-10">
        <QuestionDisplay
          questionText={question.text}
          questionNumber={question.number}
          totalQuestions={startData.totalQuestions}
          isSpeaking={phase === 'speaking'}
        />

        {phase === 'evaluating' && (
          <div className="flex flex-col items-center gap-3">
            <div className="w-10 h-10 border-4 border-indigo-200 border-t-indigo-600 rounded-full animate-spin" />
            <p className="text-sm text-slate-500">Evaluating your answer...</p>
          </div>
        )}

        {(phase === 'speaking' || phase === 'ready' || phase === 'recording') && (
          <MicrophoneButton
            isRecording={isRecording}
            disabled={phase === 'speaking'}
            onClick={handleMicClick}
            hint={micHint}
          />
        )}

        {phase === 'evaluated' && (
          <>
            <EvaluationCard
              transcribedAnswer={result?.transcribedAnswer}
              evaluation={result?.evaluation}
            />
            <div className="flex flex-col sm:flex-row gap-3">
              {!isLast && nextQuestion && (
                <button
                  type="button"
                  onClick={handleNextQuestion}
                  className="px-8 py-2.5 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition-colors"
                >
                  Next Question →
                </button>
              )}
              <button
                type="button"
                onClick={handleEndInterview}
                disabled={ending}
                className={`px-8 py-2.5 text-sm font-semibold rounded-lg transition-colors disabled:opacity-60 ${
                  isLast
                    ? 'text-white bg-indigo-600 hover:bg-indigo-700'
                    : 'text-slate-600 border border-slate-300 hover:bg-slate-200'
                }`}
              >
                {ending ? 'Finishing up...' : isLast ? 'Finish & See Results' : 'End Interview'}
              </button>
            </div>
          </>
        )}

        {(micError || apiError) && (
          <div className="bg-red-50 border border-red-200 rounded-lg px-4 py-2 max-w-md text-center">
            <p className="text-sm text-red-700">{micError || apiError}</p>
          </div>
        )}
      </main>
    </div>
  );
};

export default InterviewScreen;
