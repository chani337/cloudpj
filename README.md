# 📝 오늘의 기분 다이어리 (Mood Diary)

React, Spring Boot, FastAPI, MySQL 및 클라우드 가상 네트워크(VPC) 인프라 기반의 **AI 감정 분석 일기 서비스**입니다.

---

## 🚀 1. 프로젝트 개요 (Project Overview)

"오늘의 기분 다이어리"는 사용자가 작성한 일기 내용을 AI 감정 분석 모델(FastAPI)이 실시간 분석하여 감정 상태(기쁨, 슬픔, 분노 등)와 이모지, 맞춤 코멘트를 생성하고, 백엔드(Spring Boot) 및 데이터베이스(MySQL)에 저장하여 관리하는 풀스택 웹 애플리케이션입니다.

### 🛠️ 기술 스택 (Tech Stack)

| 구분 | 기술 스택 | 설명 |
|---|---|---|
| **Frontend** | React (Vite), Nginx | 사용자 일기 작성 UI 및 Nginx Reverse Proxy |
| **Backend** | Spring Boot 3, Java 17, Gradle | REST API 서비스, MySQL ORM, AI 모델 서비스 연동 |
| **AI Model** | FastAPI, Python 3.11 | 텍스트 기분 분석 서비스 (감정 분석 엔진 API) |
| **Database** | MySQL 8.0 | 일기 데이터 지속성 저장 및 관리 |
| **Infrastructure** | Docker, Docker Compose, VPC Infrastructure | 멀티 컨테이너 가상화 및 클라우드 가상 네트워크 |

---

## 📁 2. 프로젝트 디렉터리 구조 (Directory Structure)

```text
cloudproject/
├── frontend/                   # React 프론트엔드 애플리케이션
│   ├── src/                    # UI 컴포넌트 및 클라이언트 로직
│   ├── index.html              # HTML 템플릿
│   ├── nginx.conf              # Nginx 리버스 프록시 설정 (Production Web)
│   ├── Dockerfile              # 운영용 Multi-stage Dockerfile
│   ├── Dockerfile.dev          # 개발용 Vite Dev Server Dockerfile
│   └── vite.config.js          # Vite 빌드 및 API 프록시 설정
├── backend/                    # Spring Boot 백엔드 API 서버
│   ├── src/                    # Controller, Domain, Repository, DTO
│   ├── build.gradle            # Gradle 의존성 및 빌드 설정
│   ├── Dockerfile              # 운영용 Multi-stage Dockerfile (Jar 빌드)
│   └── Dockerfile.dev          # 개발용 Gradle bootRun Dockerfile
├── model/                      # FastAPI AI 감정 분석 모델 서버
│   ├── main.py                 # 감정 분석 처리 API 알고리즘
│   ├── requirements.txt        # Python 패키지 의존성
│   ├── Dockerfile              # 운영용 Python Dockerfile
│   └── Dockerfile.dev          # 개발용 Uvicorn reload Dockerfile
├── mysql-init/                 # 데이터베이스 초기화 스크립트
│   └── 01-schema.sql           # DDL 스크립트 및 초기 테이블 데이터
├── docker-compose.local.yml    # 운영(Prod) 환경 Docker Compose
├── docker-compose.dev.yml      # 개발(Dev Hot-Reload) 환경 Docker Compose
├── .env.example                # 환경변수 템플릿 파일
└── README.md                   # 프로젝트 통합 문서
```

---

## ☁️ 3. 클라우드 아키텍처 (Cloud Architecture)

```text
[ 사용자 (User Client) ]
       │ (HTTP:80 / HTTPS:443)
       ▼
┌────────────────────────────────────────────────────────────────────────┐
│  VPC: mood-vpc (10.0.0.0/16)                                            │
│                                                                        │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ Public Subnet: mood-public-subnet (10.0.1.0/24)                  │  │
│  │                                                                  │  │
│  │   [ Web Server ]                                                 │  │
│  │   - Server Name: mood-web-srv                                    │  │
│  │   - Services: React Web App, Nginx Reverse Proxy                 │  │
│  │   - Public IP 바인딩 / ACG: mood-web-acg                          │  │
│  └───────────────────┬──────────────────────────────────────────────┘  │
│                      │ (Internal API Forwarding: 8080)                 │
│                      ▼                                                 │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ Private Subnet: mood-private-subnet (10.0.2.0/24)                 │  │
│  │                                                                  │  │
│  │   [ App & Model Server ]                [ Database Server ]      │  │
│  │   - Server Name: mood-app-srv           - Server Name: mood-db-srv│  │
│  │   - Spring Boot API (Port: 8080)        - MySQL DB (Port: 3306)  │  │
│  │   - FastAPI Model API (Port: 8000)      - ACG: mood-db-acg       │  │
│  │   - ACG: mood-app-acg                                            │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 🔒 4. 클라우드 네트워크 및 ACG 보안 그룹 설정 (Network & Security)

### 🖥️ 4-1. VPC 및 서브넷 구획 정의
- **VPC 이름**: `mood-vpc` (`10.0.0.0/16`)
- **Public Subnet**: `mood-public-subnet` (`10.0.1.0/24`) — 외부 인터넷 트래픽을 수신하며 Nginx 기반 Web Server 배치
- **Private Subnet**: `mood-private-subnet` (`10.0.2.0/24`) — 외부에서 직접 접근할 수 없는 비공개 망으로 Application 및 Database 배치

### 🖥️ 4-2. 서버 사양 및 배치 명세 (Server List)

| 서버 이름 (Server Name) | 배치 서브넷 (Subnet) | 역할 (Role) | 탑재 서비스 (Service) | IP 바인딩 (IP Assignment) |
|---|---|---|---|---|
| **`mood-web-srv`** | Public Subnet | Web Frontend / Nginx Proxy | React, Nginx | Public IP / Private IP (`10.0.1.10`) |
| **`mood-app-srv`** | Private Subnet | Backend API & AI Model Serv | Spring Boot (8080), FastAPI (8000) | Private IP (`10.0.2.10`) |
| **`mood-db-srv`** | Private Subnet | Database Management | MySQL 8.0 (3306) | Private IP (`10.0.2.20`) |

---

### 🛡️ 4-3. ACG (Access Control Group) 규칙 명세

#### 1) Web Server ACG (`mood-web-acg`)
- **대상 서버**: `mood-web-srv` (Public Subnet)

| 구분 | 프로토콜 | 포트 범위 | 접근 대상 (Source / Destination) | 설명 |
|---|---|---|---|---|
| **Inbound** | TCP | 80 | `0.0.0.0/0` | 사용자 웹 HTTP 접근 허용 |
| **Inbound** | TCP | 443 | `0.0.0.0/0` | 사용자 웹 HTTPS 접근 허용 |
| **Inbound** | TCP | 22 | `관리자 허용 IP` | SSH 관리자 접근 허용 |
| **Outbound**| ALL | ALL | `10.0.2.0/24` (Private Subnet) | Private 서브넷 백엔드로 프록시 요청 전송 |

#### 2) Application Server ACG (`mood-app-acg`)
- **대상 서버**: `mood-app-srv` (Private Subnet)

| 구분 | 프로토콜 | 포트 범위 | 접근 대상 (Source / Destination) | 설명 |
|---|---|---|---|---|
| **Inbound** | TCP | 8080 | `mood-web-acg` (`10.0.1.0/24`) | Web 서버로부터의 REST API 호출만 허용 |
| **Inbound** | TCP | 8000 | `mood-app-acg` (Self Internal) | Spring Boot ➡️ FastAPI 모델 간 내부 연동 |
| **Inbound** | TCP | 22 | `mood-web-srv` (Bastion Host) | Web 서버를 경유한 내부 SSH 관리 접속 |
| **Outbound**| TCP | 3306 | `mood-db-acg` (`10.0.2.20`) | DB 서버로의 쿼리 요청 전송 |

#### 3) Database Server ACG (`mood-db-acg`)
- **대상 서버**: `mood-db-srv` (Private Subnet)

| 구분 | 프로토콜 | 포트 범위 | 접근 대상 (Source / Destination) | 설명 |
|---|---|---|---|---|
| **Inbound** | TCP | 3306 | `mood-app-acg` (`10.0.2.10`) | App 서버로부터의 데이터베이스 접속만 허용 |
| **Inbound** | TCP | 22 | `mood-web-srv` (Bastion Host) | 내부 관리용 SSH 접속 |
| **Outbound**| ALL | ALL | `10.0.2.0/24` | 내부 트래픽 응답 |

---

## ⚡ 5. 서비스 실행 방법 (Execution)

### 5-1. 환경 변수 구성
`.env.example` 파일을 복사하여 환경 변수 파일 생성:
```bash
cp .env.example .env
```

### 5-2. 운영 모드 실행 (Production)
멀티스테이지 이미지 빌드 후 컨테이너를 실행합니다.
```bash
docker compose -f docker-compose.local.yml up -d --build
```
- **Frontend Web**: `http://localhost:3000`
- **Backend Health Check**: `http://localhost:8080/api/health`

### 5-3. 개발 모드 실행 (Development / Hot-Reload)
소스 코드 수정이 실시간 반영되는 개발 환경입니다.
```bash
docker compose -f docker-compose.dev.yml up --build
```
- **Frontend Dev Server**: `http://localhost:5173`
- **Backend API**: `http://localhost:8080/api/health`

---

## 📡 6. API 명세 (API Reference)

### 백엔드 REST API (`Spring Boot`)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | 백엔드 서비스 상태 점검 |
| `GET` | `/api/diary` | 등록된 전체 일기 목록 조회 |
| `POST` | `/api/diary` | 신규 일기 작성 (AI 모델 기분 분석 및 저장) |
| `DELETE` | `/api/diary/{id}` | 일기 데이터 삭제 |

### AI 모델 서비스 API (`FastAPI`, Internal)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/health` | 모델 서비스 상태 점검 |
| `POST` | `/analyze` | 텍스트 기분 분석 및 라벨/이모지/코멘트 반환 |
