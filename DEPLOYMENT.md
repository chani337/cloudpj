# 🛠️ 클라우드 멀티 서버 배포 가이드 (Deployment Guide)

본 문서는 **Public Server (Frontend / Web)**와 **Private Server (Backend / AI Model / DB)** 분리 배포 환경에서 각 서버별 CLI 명령어 실행 순서를 정리한 가이드입니다.

---

## 🌐 1. Public Server (프론트엔드 / Nginx 웹 서버)

- **서버 이름**: `mood-web-srv`
- **배치 위치**: Public Subnet (`10.0.1.0/24`)
- **주요 역할**: 외부 유저 접속(포트 80/443) 수신, React Static 애플리케이션 호스팅, Nginx 기반 `/api/*` 프록시 요청 전송

### 📜 명령어 실행 순서

```bash
# 1. 시스템 패키지 목록 업데이트 및 필수 도구(Git, Docker) 설치
sudo apt-get update -y
sudo apt-get install -y git docker.io docker-compose-v2

# 2. Docker 서비스 활성화 및 현재 사용자 권한 설정
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
newgrp docker

# 3. 프로젝트 저장소 클론 및 디렉터리 이동
git clone https://github.com/chani337/cloudpj.git
cd cloudpj

# 4. 프론트엔드 컨테이너 빌드 및 구동
cd frontend
docker build -t mood-frontend .
docker run -d -p 80:80 --restart unless-stopped --name mood-frontend mood-frontend

# 5. 웹 서버 동작 및 프로세스 상태 점검
docker ps
curl http://localhost
```

---

## 🔒 2. Private Server (백엔드 API / AI 모델 / DB 서버)

- **서버 이름**: `mood-app-srv` & `mood-db-srv`
- **배치 위치**: Private Subnet (`10.0.2.0/24`)
- **주요 역할**: 외부 직접 접근 차단. Public Server로부터 전달되는 내부 REST API 요청 및 AI 모델 분석, 데이터베이스 저장 처리 (포트 8080, 8000, 3306)

### 📜 명령어 실행 순서

```bash
# 1. 시스템 패키지 목록 업데이트 및 필수 도구(Git, Docker) 설치
sudo apt-get update -y
sudo apt-get install -y git docker.io docker-compose-v2

# 2. Docker 서비스 활성화 및 현재 사용자 권한 설정
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
newgrp docker

# 3. 프로젝트 저장소 클론 및 디렉터리 이동
git clone https://github.com/chani337/cloudpj.git
cd cloudpj

# 4. 환경 변수 파일 생성 (.env)
cp .env.example .env

# 5. Spring Boot 백엔드 + FastAPI AI 모델 + MySQL DB 일괄 빌드 및 백그라운드 실행
docker compose -f docker-compose.local.yml up -d --build

# 6. 컨테이너 구동 상태 및 헬스체크 확인
docker compose -f docker-compose.local.yml ps
curl http://localhost:8080/api/health

# 7. 서비스 통합 로그 실시간 확인
docker compose -f docker-compose.local.yml logs -f
```

---

## ⚡ 3. 빠른 참조 표 (Quick Reference)

| 서버 구분 | 주요 역할 | 한 줄 실행 가이드 |
|---|---|---|
| **Public Server** | Web UI & Proxy | `git clone` ➡️ `cd frontend` ➡️ `docker build -t mood-frontend .` ➡️ `docker run -d -p 80:80 ...` |
| **Private Server** | API / Model / DB | `git clone` ➡️ `cp .env.example .env` ➡️ `docker compose -f docker-compose.local.yml up -d --build` |
