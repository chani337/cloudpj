# 오늘의 기분 다이어리 (Mood Diary) — Docker & Docker Compose 실습 프로젝트

React + Spring Boot + FastAPI + MySQL을 묶어서, "한 줄 일기를 쓰면 AI가 오늘의 기분을 분석해주는"
아주 작은 풀스택 서비스를 만듭니다. 실제 학습된 AI 모델 대신 키워드 기반의 장난스러운 규칙 모델을 사용하지만,
구조는 실제 ML 모델 서빙과 동일하게 짜여 있어 나중에 진짜 모델로 교체하기 쉽습니다.

```
사용자 ──▶ [React] ──/api──▶ [Spring Boot] ──▶ [MySQL]
                                   │
                                   └──/analyze──▶ [FastAPI mood-model]
```

| 레이어 | 기술 | 역할 |
|---|---|---|
| Frontend | React (Vite) | 일기 작성 UI, 목록 조회 |
| Backend | Spring Boot 3 | REST API, MySQL 저장, 모델 서비스 호출 |
| Model | FastAPI (Python) | 텍스트 기분 분석 (데모용 규칙 기반) |
| DB | MySQL 8 | 일기 데이터 저장 |

---

## 1. 리포지토리 구성 (Public Frontend / Private Backend)

수업에서는 아래처럼 **2개의 GitHub 리포지토리**로 나누는 것을 권장합니다.

- `cloudproject-frontend` **(Public)** → 이 프로젝트의 `frontend/` 폴더
- `cloudproject-backend` **(Private)** → 이 프로젝트의 `backend/`, `model/`, `mysql-init/`, 그리고 compose 파일들

실습 시에는 두 리포지토리를 아래처럼 **같은 부모 폴더**에 나란히 clone 하면,
지금 이 프로젝트와 동일한 상대 경로 구조가 됩니다.

```
cloudproject/
├── cloudproject-frontend/     (public repo)
│   └── ...  ← frontend/ 내용
└── cloudproject-backend/      (private repo)
    ├── backend/
    ├── model/
    ├── mysql-init/
    ├── docker-compose.local.yml
    ├── docker-compose.dev.yml
    └── .env.example
```

이 경우 compose 파일의 `build.context` 경로(`./frontend`, `./backend`, `./model`)만
리포지토리 분리 구조에 맞게 수정해주면 됩니다. 처음 실습할 때는 지금처럼
하나의 폴더 안에 다 넣고 시작해도 무방합니다 (모노레포 방식).

---

## 2. 사전 준비

```bash
cp .env.example .env
```

---

## 3. 실행 방법

### 3-1. LOCAL (운영과 동일하게, 빌드된 이미지로 실행)

```bash
docker compose -f docker-compose.local.yml up --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/health
- Model: 컨테이너 내부 네트워크에서만 접근 (mood-model:8000)
- MySQL: localhost:3306

소스 코드를 고치면 다시 빌드해야 반영됩니다.

```bash
docker compose -f docker-compose.local.yml up -d --build
docker compose -f docker-compose.local.yml down
```

### 3-2. DEV (개발용, 코드 수정 시 즉시 반영)

```bash
docker compose -f docker-compose.dev.yml up --build
```

- Frontend (Vite Dev Server, HMR): http://localhost:5173
- Backend (Gradle bootRun): http://localhost:8080/api/health
- Model (uvicorn --reload): 컨테이너 내부 네트워크에서만 접근

`frontend/`, `backend/`, `model/` 폴더의 코드를 저장하면 컨테이너 재시작 없이 바로 반영됩니다.

```bash
docker compose -f docker-compose.dev.yml down
```

---

## 4. 무엇을 가르치기 좋은가 (수업 포인트)

1. **멀티스테이지 Dockerfile**: `backend/Dockerfile`, `frontend/Dockerfile` — 빌드 이미지와 실행 이미지를 분리해 최종 이미지 용량을 줄이는 예시
2. **dev vs local(prod) Dockerfile 분리**: 같은 서비스라도 `Dockerfile` / `Dockerfile.dev`로 목적에 따라 다르게 구성
3. **Bind Mount vs Volume**: dev compose는 소스코드를 Bind Mount, MySQL 데이터는 Volume으로 분리
4. **서비스 간 통신**: 컨테이너 이름(`mysql`, `mood-model`, `backend`)으로 서로를 찾는 Docker Network / DNS
5. **Nginx 리버스 프록시**: local 환경에서 `frontend`(nginx)가 `/api/*` 요청을 `backend`로 프록시
6. **환경변수 분리**: `.env`로 DB 계정, 서비스 URL 등을 코드와 분리
7. **healthcheck / depends_on**: MySQL이 준비된 후 backend가 뜨도록 순서 제어

---

## 5. API 요약

| Method | URL | 설명 |
|---|---|---|
| GET | `/api/health` | 백엔드 헬스체크 |
| GET | `/api/diary` | 전체 일기 목록 (최신순) |
| POST | `/api/diary` | 일기 작성 (`{ "content": "..." }`) → 모델 분석 후 저장 |
| DELETE | `/api/diary/{id}` | 일기 삭제 |

FastAPI 모델 서비스 (`mood-model`, 내부 전용):

| Method | URL | 설명 |
|---|---|---|
| GET | `/health` | 모델 서비스 헬스체크 |
| POST | `/analyze` | `{ "text": "..." }` → `{ mood, emoji, score, comment }` |

---

## 6. 다음 확장 아이디어 (수업 심화용)

- `mood-model`을 진짜 sklearn/transformers 감정분석 모델로 교체
- Nginx에 HTTPS(TLS) 적용
- CI에서 이미지 빌드 → 사설 Registry에 push
- Kubernetes로 전환해보기
