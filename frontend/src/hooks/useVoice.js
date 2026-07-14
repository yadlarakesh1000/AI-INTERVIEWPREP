import { useCallback, useRef, useState } from 'react';

// Microphone capture via the MediaRecorder API (Section 12/14).
// States: idle -> recording -> (stop) -> audioBlob ready.
const useVoice = () => {
  const mediaRecorderRef = useRef(null);
  const streamRef = useRef(null);
  const chunksRef = useRef([]);

  const [isRecording, setIsRecording] = useState(false);
  const [audioBlob, setAudioBlob] = useState(null);
  const [error, setError] = useState('');

  const startRecording = useCallback(async () => {
    setError('');
    setAudioBlob(null);
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      streamRef.current = stream;
      chunksRef.current = [];

      // Chrome records WebM/Opus by default, which Groq Whisper accepts
      // directly (Section 14) — no conversion needed.
      const recorder = new MediaRecorder(stream);
      mediaRecorderRef.current = recorder;

      recorder.ondataavailable = (e) => {
        if (e.data && e.data.size > 0) chunksRef.current.push(e.data);
      };
      recorder.onstop = () => {
        const blob = new Blob(chunksRef.current, {
          type: recorder.mimeType || 'audio/webm',
        });
        setAudioBlob(blob);
        // Release the microphone so the browser's "recording" indicator goes away.
        streamRef.current?.getTracks().forEach((track) => track.stop());
        streamRef.current = null;
        setIsRecording(false);
      };

      recorder.start();
      setIsRecording(true);
    } catch (err) {
      if (err.name === 'NotAllowedError' || err.name === 'PermissionDeniedError') {
        setError('Microphone access was denied. Please allow microphone access and try again.');
      } else if (err.name === 'NotFoundError') {
        setError('No microphone was found on this device.');
      } else {
        setError('Could not start recording. Please check your microphone and try again.');
      }
      setIsRecording(false);
    }
  }, []);

  const stopRecording = useCallback(() => {
    const recorder = mediaRecorderRef.current;
    if (recorder && recorder.state === 'recording') {
      recorder.stop();
    }
  }, []);

  const resetRecording = useCallback(() => {
    setAudioBlob(null);
    setError('');
  }, []);

  return { startRecording, stopRecording, resetRecording, audioBlob, isRecording, error };
};

export default useVoice;
