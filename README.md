# 오늘의 기분 다이어리 (Mood Diary)

React, Spring Boot, FastAPI, MySQL 및 클라우드 가상 네트워크(VPC) 인프라 기반의 **AI 감정 분석 일기 서비스**입니다.

---

## 1. 프로젝트 개요 (Project Overview)

"오늘의 기분 다이어리"는 사용자가 작성한 일기 내용을 AI 감정 분석 모델(FastAPI)이 실시간 분석하여 감정 상태(기쁨, 슬픔, 분노 등)와 이모지, 맞춤 코멘트를 생성하고, 백엔드(Spring Boot) 및 데이터베이스(MySQL)에 저장하여 관리하는 풀스택 웹 애플리케이션입니다.

### 기술 스택 (Tech Stack)

| 구분 | 기술 스택 | 설명 |
|---|---|---|
| **Frontend** | React (Vite), Nginx | 사용자 일기 작성 UI 및 Nginx Reverse Proxy |
| **Backend** | Spring Boot 3, Java 17, Gradle | REST API 서비스, MySQL ORM, AI 모델 서비스 연동 |
| **AI Model** | FastAPI, Python 3.11 | 텍스트 기분 분석 서비스 (감정 분석 엔진 API) |
| **Database** | MySQL 8.0 | 일기 데이터 지속성 저장 및 관리 |
| **Infrastructure** | Docker, Docker Compose, VPC Infrastructure | 멀티 컨테이너 가상화 및 클라우드 가상 네트워크 |

---

## 2. 프로젝트 디렉터리 구조 (Directory Structure)

```text
cloudpj/
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
