# AI InterviewPrep

An AI-powered interview preparation platform built around **voice-based mock interviews**. The AI asks a question out loud, you answer with your microphone, and your response is transcribed, evaluated across a four-dimension rubric, and used to generate the next question — just like a real interview.

Alongside interview practice, the platform offers career roadmaps, a day-by-day skill tracker, AI skill recommendations, and performance analytics.

---

## Features

- **Voice-based mock interviews** — three types (HR & Communication, Resume-Based, CS Fundamentals) at two difficulty levels
- **Full voice pipeline** — text-to-speech question delivery → microphone capture → speech-to-text transcription → LLM evaluation → follow-up generation
- **Four-dimension evaluation rubric** — every answer is scored 1–10 on Relevance, Clarity & Structure, Depth / Technical Accuracy, and Confidence & Fluency, each with written feedback
- **Context-aware follow-ups** — on Medium difficulty the AI asks follow-up questions based on what you actually said
- **Resume parsing** — upload a PDF, and skills, projects, and experience are extracted and used to generate personalised questions
- **Career roadmaps** — curated 26- or 52-week learning paths with weekly plans, milestones, and recommended projects
- **Skill tracker** — day-by-day checklist tied to your active roadmap, with per-week and overall progress
- **AI skill recommendations** — suggested next skills based on your current stack and career goal
- **Analytics & history** — score trends across your last five interviews with improvement suggestions

---

## Tech Stack

### Frontend
| Technology | Purpose |
|---|---|
| React 18 + Vite | UI and build tooling |
| React Router 6 | Routing |
| Redux Toolkit | Auth state |
| TanStack React Query 5 | Server state and caching |
| Tailwind CSS | Styling |
| Recharts | Analytics charts |
| Axios | HTTP client with JWT interceptor |
| MediaRecorder API | Microphone capture |

### Backend
| Technology | Purpose |
|---|---|
| Java 17 + Spring Boot 3.2 | Application framework |
| Spring Security + JJWT | JWT authentication |
| Spring Data JPA | Persistence |
| MySQL 8.0 | Database |
| MapStruct + Lombok | Mapping and boilerplate reduction |
| Apache PDFBox | Resume text extraction |

### AI Services
| Service | Provider | Model |
|---|---|---|
| Question generation & answer evaluation | Groq | `llama-3.3-70b-versatile` |
| Speech-to-text | Groq Whisper | `whisper-large-v3-turbo` |
| Text-to-speech | Google Gemini | Flash TTS |

All AI capabilities sit behind Java service interfaces, so providers can be swapped without touching controllers or the frontend.

---

## Architecture

A modular monolith with a straightforward layered flow:

```
HTTP Request → Controller → Service → Repository → Database
```

```
┌──────────────┐     ┌──────────────────────┐     ┌────────────┐
│   React      │────▶│   Spring Boot        │────▶│   MySQL    │
│   (port 5173)│◀────│   (port 8080)        │◀────│  (3306)    │
└──────────────┘     │   ┌──────────────┐   │     └────────────┘
                     │   │ AI Services  │   │
                     │   │ (interfaces) │   │
                     │   └──────┬───────┘   │
                     └──────────┼───────────┘
                    ┌───────────┼───────────┐
              ┌─────▼───┐ ┌─────▼───┐ ┌────▼────┐
              │  Groq   │ │  Groq   │ │ Gemini  │
              │  LLM    │ │ Whisper │ │  TTS    │
              └─────────┘ └─────────┘ └─────────┘
```

Interview sessions (the in-flight question/answer history used for follow-ups) are held **in memory**; only the final summary is persisted. No transcripts are stored.

---

## Prerequisites

- **Java 17** (JDK 17 — not 21)
- **Node.js 18+** and npm
- **MySQL 8.0**
- **Maven 3.8+** (or use the bundled wrapper)
- A modern browser with microphone support (Chrome recommended)

You will also need two free API keys:

| Key | Where to get it | Cost |
|---|---|---|
| `GROQ_API_KEY` | [console.groq.com](https://console.groq.com) | Free, no card required |
| `GEMINI_API_KEY` | [ai.google.dev](https://ai.google.dev) | Free tier |

---

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/yadlarakesh1000/AI-INTERVIEWPREP.git
cd AI-INTERVIEWPREP
```

### 2. Create the database

```sql
CREATE DATABASE interviewprep;
```

Or with Docker:

```bash
docker run --name interviewprep-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=interviewprep \
  -p 3306:3306 -d mysql:8.0
```

Tables are created automatically on first startup (`ddl-auto: update`), and the HR question bank and roadmap templates are seeded on boot.

### 3. Configure the backend

```bash
cd backend
cp .env.example .env
```

Edit `.env` with your values:

```bash
# Database
DB_URL=jdbc:mysql://localhost:3306/interviewprep
DB_USERNAME=root
DB_PASSWORD=your_mysql_password_here

# JWT — any random string, 32+ characters
JWT_SECRET=change_me_to_a_long_random_secret_at_least_32_characters

# AI providers
GROQ_API_KEY=your_groq_api_key_here
GEMINI_API_KEY=your_gemini_api_key_here

# Resume uploads
RESUME_UPLOAD_DIR=./uploads/resumes
```

> `.env` is gitignored — never commit real keys. Only `.env.example` belongs in version control.

### 4. Configure the frontend

```bash
cd ../frontend
cp .env.example .env
npm install
```

The default `frontend/.env` points at the local backend:

```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## Running Locally

Start MySQL, then run the backend and frontend in separate terminals.

**Terminal 1 — backend** (http://localhost:8080)

```bash
cd backend
mvn spring-boot:run
```

**Terminal 2 — frontend** (http://localhost:5173)

```bash
cd frontend
npm run dev
```

Open http://localhost:5173, register an account, complete your profile, and start an interview.

> **Microphone access:** browsers only grant microphone permission on `localhost` or over HTTPS. Grant the permission prompt when starting your first interview.

### Production build

```bash
cd backend  && mvn clean package     # → target/*.jar
cd frontend && npm run build         # → dist/
```

---

## API Overview

All endpoints are prefixed with `/api`. Everything except the auth endpoints requires an `Authorization: Bearer <token>` header. Responses use a consistent envelope:

```json
{ "success": true, "message": "...", "data": { } }
```

### Auth
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/register` | Create an account, returns tokens |
| `POST` | `/auth/login` | Sign in |
| `POST` | `/auth/refresh` | Exchange a refresh token for a new access token |

### Profile
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/profile` | Current user's profile |
| `POST` | `/profile` | Create profile |
| `PUT` | `/profile` | Update profile |

### Resume
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/resumes/upload` | Upload a PDF (multipart, max 5 MB) and parse it |
| `GET` | `/resumes/latest` | Most recent resume with parsed data |

### Interviews
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/interviews/start` | Begin an interview, returns the first question |
| `POST` | `/interviews/{id}/answer` | Upload recorded audio, get transcript + evaluation + next question |
| `POST` | `/interviews/{id}/end` | Finish and persist the summary |
| `GET` | `/interviews/audio/{sessionId}/{questionKey}` | TTS audio for a question |

### Roadmaps & Skill Tracker
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/roadmaps` | All roadmap templates |
| `GET` | `/roadmaps/{id}` | Full roadmap with weekly plan |
| `POST` | `/roadmaps/select` | Select a roadmap as active |
| `GET` | `/roadmaps/my` | Currently active roadmap |
| `GET` | `/skill-tracker/week/{n}` | A week's day-by-day progress |
| `PUT` | `/skill-tracker/toggle` | Mark a day complete/incomplete |
| `GET` | `/skill-tracker/overview` | Overall progress summary |

### Insights
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/skill-recommendations` | AI-suggested next skills |
| `GET` | `/analytics` | Score averages, trend, and last five interviews |
| `GET` | `/history` | Last five completed interviews |

---

## Screenshots

> _Screenshots to be added._

| | |
|---|---|
| **Dashboard** <br> `docs/screenshots/dashboard.png` | **Interview** <br> `docs/screenshots/interview.png` |
| **Evaluation** <br> `docs/screenshots/evaluation.png` | **Analytics** <br> `docs/screenshots/analytics.png` |
| **Roadmaps** <br> `docs/screenshots/roadmaps.png` | **Skill Tracker** <br> `docs/screenshots/skill-tracker.png` |

---

## Project Structure

```
.
├── backend/
│   ├── src/main/java/com/interviewprep/
│   │   ├── config/          # App, CORS, and AI configuration
│   │   ├── security/        # JWT provider, filter, security config
│   │   ├── common/          # ApiResponse, constants
│   │   ├── exception/       # Global exception handling
│   │   └── modules/         # Feature modules (auth, profile, interview, …)
│   │       └── <module>/    # controller · service · repository · entity · dto · mapper
│   └── src/main/resources/
│       └── application.yml
└── frontend/
    └── src/
        ├── api/             # Axios instance with JWT interceptor
        ├── store/           # Redux store and auth slice
        ├── hooks/           # useAuth, useApi, useVoice
        ├── components/      # Feature components
        └── pages/           # Route-level pages
```

Each backend module is self-contained, following `Controller → Service → Repository → Entity`.

---

## Roadmap (Phase 2)

Phase 1 (this repository) is a complete, working product built on direct LLM calls. Phase 2 focuses on **improving AI quality rather than adding features**, and is confined to the backend AI layer — the API contracts and frontend stay unchanged.

- **RAG-based interview engine** — replace direct prompting with retrieval-augmented generation over curated question banks, CS fundamentals material, and parsed resume content, using a vector database for retrieval
- **GitHub-aware project interviews** — a fourth interview type where you supply a repository URL and the AI analyses the README, structure, and dependencies to ask genuinely project-specific questions
- **Richer avatar** — waveform-driven lip sync and expressions that react to evaluation scores

Because every AI capability already sits behind a service interface, Phase 2 implementations can be swapped in without touching controllers or the frontend.

---

## Notes & Limitations

- Interview sessions live in memory, so a backend restart mid-interview ends that session — acceptable for the project's scope.
- Only the **five most recent completed interviews** are retained per user; older records are pruned automatically.
- Answer transcripts are never persisted — only scores and summaries.
- Free-tier AI rate limits can occasionally slow evaluation; the app retries once and degrades gracefully with user-facing messages.

---

## License

Built as a final-year B.Tech project. Free to use for learning and reference.
