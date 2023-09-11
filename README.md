# 쿡슝(Cook-Shoong)
![](https://velog.velcdn.com/images/eora21/post/9b2cc6a2-84df-4ff6-9c1c-338c53c7f22e/image.png)

## 팀원
### [김주호 (eora21)](https://github.com/eora21)
### [유승연 (seong1542)](https://github.com/seong1542)
### [윤동현 (Dendroh)](https://github.com/Dendroh)
### [정제완 (JeongJeWan)](https://github.com/JeongJeWan)
### [추만석 (10kseok)](https://github.com/10kseok)

## ERD
https://www.erdcloud.com/d/5D89pNAP23LAuexGz

### DB관리자
- 유승연

ERD 작성은 ERDCloud를 사용하였으며, 생성 및 수정은 My Workbench를 이용하였습니다. 해당 DB의 DDL파일은 이곳을 참조해 주세요. -> https://github.com/nhnacademy-be3-CookShoong/ddl_manage

Cook-Shoong ERD 0.2.8 version까지 업데이트되었습니다.


![스크린샷 2023-08-19 오후 8 36 20](https://github.com/nhnacademy-be3-CookShoong/.github/assets/66362713/1d51e50a-18aa-4920-b179-75a030591ff6)


## 아키텍처
- 클라이언트의 요청은 NginX 통해서 들어오고, 로드밸런스에서 Round Robin 방식으로 순서대로 Front Application에 보내지게 됩니다.
- Front Application 은 필요한 요청을 API Gateway를 통해 처리하고, Gateway는 해당 요청에 대해 처리되어야 하는 서비스 API로 요청을 보내게 됩니다.
- 이때 Service Discovery 인 Eureka 에서 필요한 서비스가 어느 곳에 있는지에 대한 정보를 API Gateway로 반환하고 API Gateway는 이에 따라 해당 API 서비스를 호출하고 결과를 받게 됩니다.
- 해당 API 서비스는 Auth, Shop 이 있습니다. 이 외에, 검색 기능 향상을 위한 엘락스틱 서치 서버 대용량 일괄처리를 위한 배치 서버가 존재합니다.

![CookShoong_아키텍처](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/e644a030-23cf-4e91-9319-7e45d905893a)

## CI/CD
1. `Github`을 통해 코드베이스를 관리하며 기본적으로 `Github Action`을 통한 CI를 진행하여 빌드하고 테스트가 진행되어 성공시 통합과정이 이뤄집니다.
   - 원격 레포지토리를 사용함에 따라 DB 정보 등 암호화가 필요한 정보들은 NHN Cloud의 Secure Key Manager를 사용하여 관리되고 환경변수를 통해 정보에 접근합니다.
   - `NHN Dooray` 서비스의 WebHook 설정을 통해 CI 관련 알림을 받고 확인할 수 있습니다.
   - Google Checks를 변형한 Checkstyle을 사용하여 팀원들끼리의 코드 일관성을 유지하며 재사용성을 높였습니다.
2. CI 과정이 일어나고 검증을 마친 코드들은 `Docker` 이미지로 생성되며 `Github Action` 또는 Jenkins를 통해 자동으로 `NHN Cloud Instance`에 `Docker Container`가 생성되어 배포가 이뤄집니다.
   - CD 과정에서 정적 분석 툴인 `SonarQube`을 사용하여 코드 품질을 측정하며 이상 있는 코드들을 감지합니다.

![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/0cf8b365-230e-4040-98d9-04b0323c8a50)

## Black Box - Client Test Flow
- 사이트에 처음 방문하는 회원이 사용할 수 있는 기능들에 대해 실험해 보는 블랙박스테스트 플로우입니다.

![Client_Test_Flow](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/3caa5da3-914e-47fe-86eb-05e4133acb71)

## 프로젝트 관리
체계적이고 효율적인 관리를 위해, [Github Project](https://github.com/orgs/nhnacademy-be3-CookShoong/projects/1)을 활용하여 프로젝트를 진행하였습니다.

### Kanban
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/61442066/d4023219-5576-4f5a-a13e-7f5a84284ce3)

Kan ban 보드를 활용하여 현재 프로젝트의 진행도를 수시로 확인할 수 있었습니다.

### Scrum
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/61442066/26c89680-9a95-42ce-b413-95af2dfbcd60)

스크럼은 평일, 하루에 한 번씩 진행하였습니다. 아침에 서로의 진행 상황을 이야기하고, 필요하다면 오후에도 스스럼없이 의견을 나누는 시간을 가졌습니다.

### Share
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/61442066/11ff915c-5988-4013-ab7b-d9fde6dfb515)

서로 공부한 상황에 대해 수시로 공유하였습니다. 만약 모여서 이야기를 할 수 없는 상황일 경우, STUDY 또는 REFERENCE 항목에 해당하는 글을 남겨 확인하였습니다.

### Collaboration
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/61442066/8c2e34d5-6d25-4199-9bf6-d2a6584bc35e)

결정하거나 진행해야 할 일이 있다면 만료일자를 설정하여 해당 기간 안에 마치도록 하였습니다. 설령 마치지 못했다면, delay로 넘겨 꾸준히 관리할 수 있도록 하였습니다.

### Manage
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/61442066/6750030e-a8b1-453e-9823-1f23c09e585b)

![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/61442066/26e59416-9a87-489d-a549-3532ce2a1d32)

항목마다 프로젝트를 생성하여 관리했으며, git-flow 전략을 활용해 진행중인 작업별로 브랜치를 나누었습니다.

각각 개발중인 내용은 feature에 작성하였으며, 해당 작업이 끝났을 경우 develop으로 병합하였습니다. 병합된 내용들은 main에 올려 실제 배포서버로 배포하였습니다.

### PR
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/61442066/8c1f5600-5cbe-4369-90d9-a3324e00bbb2)

PR 제목은 해당 작업을 잘 나타낼 수 있도록 하였으며, 2명 이상의 approve를 받았을 시 merge할 수 있도록 하였습니다.

### CodeReview
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/61442066/2db4b538-f6bf-452b-8a8f-c03c8fe16ac6)

PR에 대해 코드리뷰를 진행하였으며, 오프라인으로 진행했더라도 온라인에 기록을 남겨 서로가 확인할 수 있도록 하였습니다.

## 일정관리 - [WBS](https://docs.google.com/spreadsheets/d/1OSbKIHQUBWEuf-fbNGP96Cmv5vJj0ipUISgu_yhQV1Y/edit#gid=893001414)
회원과 배송, 매장과 리뷰, 메뉴, 쿠폰과 포인트, 주문과 결제로 크게 나누어 업무를 분담하였습니다. 각 파트에서 주요 기능별로 업무 분리 후 세부 업무 목록을 작성하였고, 주요 업무를 수행하면서 사용하게 되는 예상 사용 기술들을 같이 기재하였습니다. 이후 배달 어플리케이션에서 반드시 필요한 (회원, 매장, 메뉴, 쿠폰)을 우선적으로 작업하였고, (주문, 결제, 포인트, 리뷰, 배송) 순으로 업무 진행 순서를 정했습니다.

![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/76582376/55227907-d603-45f4-a488-40e01ea19fb2)
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/76582376/05f4a57b-fdc6-4814-ab09-175927cb65ad)

## [Github Projects](https://github.com/orgs/nhnacademy-be3-CookShoong/projects/1) 링크, Github Roadmap 관리
- WBS 일정을 바탕으로 각자 일정에 맞추어 세분화 작업을 진행하였습니다. 추가로 각자 진행하는 부분에 있어서 사전 지식 및 진행 사항 등을 일정 안에 작성함으로써 프로젝트 효율을 높였습니다.
- 추만석
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/0d99356e-f8bb-4a7f-9523-e2b68b835188)
- 윤동현
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/cf01017f-fc7b-4016-a6c7-91aa6a831a8b)
- 김주호
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/8bda05b3-7fea-4b35-8a84-9fa744163686)
- 정제완
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/1adbf6eb-e8fb-46d6-b59b-4793476591c6)
- 유승연
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/eb275314-3e9a-4be6-a14b-505250105167)

## 칸반보드
- Git에서 제공하는 칸반보드를 활용해서 해야 할 일, 진행 중, 지연, 완료 단계로 프로젝트 효율을 높였습니다.
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/5907ea4e-721a-4db0-9a8d-9188a297c35b)

## 테스트 커버리지
- Shop 서버의 주요 기능들에 대한 테스트 커버리지입니다.
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/93ebfd8b-76f6-418d-b5f6-f0d03145acdd)

## REST API Specification
- SwaggerUI + Spring REST Docs 함께 사용하였습니다.
- [REST API](https://www.cookshoong.store/swagger-ui)
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/d2659fac-288c-45f6-ba75-018ca5cfe9b6)

## [QA](https://github.com/orgs/nhnacademy-be3-CookShoong/projects/1/views/18?pane=issue&itemId=35665501)
- CookShoong에 'Quality Assurance'을 진행하여 문제가 될 수 있는 부분을 종합하고 해결하였습니다.
![image](https://github.com/nhnacademy-be3-CookShoong/.github/assets/85005950/bdfcf19f-a7fa-4986-8787-d35ce4ae5e35)


# 주요기능

## 인증/인가
> Cookshoong 서비스 이용자임을 증명을 확인하고 서비스를 이용할 수 있게 합니다.

### 담당자
- 추만석

### 기능
- 인증서버에서 자격증명을 확인하고 JWT을 발급합니다.
- 액세스토큰과 리프레쉬 토큰은 JWT로 발급되며 사용자는 리프레쉬 토큰을 통해 토큰들을 자동으로 재발급합니다.
- 게이트웨이에서 토큰을 검증하여 인증된 사용자만이 백엔드 API 호출을 가능하게 합니다.
- 프론트서버에서는 발급받은 토큰을 이용한 백엔드 API 호출을 합니다.
- 만료된 토큰 또는 탈취된 토큰의 접근을 막고 관리합니다. (블랙리스트 관리)
- 리프레쉬 토큰은 암호화되어 관리됩니다.

### 사용된 기술
- Spring Security, Spring Cloud Gateway, JWT


---


## 회원
> 사용자가 Cookshoong 서비스에 간편하게 가입할 수 있도록 돕습니다.

### 담당자
- 추만석

### 기능
- Cookshoong 서비스를 이용하기 위한 회원가입이 가능합니다.
- OAuth2에서 받은 권한을 통해 얻어낸 정보로 간편 회원가입이 가능합니다.
- 등록된 자신의 정보를 수정할 수 있습니다.
- 일반 회원, 사업자 회원, 관리자로 나눠서 관리하며 계층구조를 이룹니다.
- 일반 회원은 매장 또는 메뉴 조회, 주문 등의 권한을 가집니다.
- 사업자 회원은 일반 회원의 상위 권한이며 매장을 등록 및 관리 등을 할 수 있습니다.
- 관리자는 일반, 사업자 회원보다 높은 권한을 가지며 서비스 이용에 모든 권한을 가집니다.

### 사용된 기술
- Spring Security, Spring Cloud Gateway, JWT

### 담당자
- 유승연

### 기능
- 회원의 마지막 로그인 기록이 90일 이전일 경우 스프링배치와 스케줄러를 이용하여 회원을 휴면상태로 변경함.
- 스프링배치를 이용하여 매월 5일 회원의 이전 달에 주문완료(COMPLETE)된 주문들의 횟수를 기준으로 회원의 등급을 재산정함.
- 재산정한 등급을 기준으로 매월 5일 각 등급에 따라 쿠폰을 발급.

### 사용된 기술 및 API
스프링배치, Mybatis, Quartz 스케줄러

---


## 주소
> 사용자가 해당 주소를 사용함으로써 편리하게 배달을 받을 수 있고, 매장 또한 주소를 등록해 놓음으로써 사용자가 쉽게 위치를 알 수 있다.

### 담당자
- 정제완

### 설명
- 회원
   - kakao map을 활용하여 회원가입 시 등록할 수 있도록 구현했습니다.
   - 마이페이지에서 주소 페이지에서 주소를 추가 등록하고 지도를 불러와 시각화 및 현재 위치 버튼을 통해 현재 위치를 불러오는 기능을 구현했습니다.
   - 회원에 대한 주소는 최대 10개까지 등록이 가능하도록 설정되어 있습니다.
   - 또한 회원이 선택한 주소가 메인 주소가 되어 매장이 보이게 됩니다.
   - 매장은 사용자에 주소로부터 3km 이내 위치한 매장을 불러옵니다.
- 사장님
   - 사장님 매장 등록 시 kakao map 활용할 수 있도록 구현했습니다.

### 사용된 기술 및 API
- kakao map API, JPA, Thymeleaf, javascript, Mysql

---



## 매장
> 사업자는 매장을 쉽게 등록하고 영업을 관리할 수 있고, 사용자는 본인이 주문할 수 있는 매장에 대한 가게 정보를 볼 수 있습니다.

### 담당자
- 유승연

### 기능
- 사업자 : 매장 조회, 등록 및 수정, 매장의 상태 변경, 본인 매장 리스트 관리, 영업시간과 휴무 시간 기준으로 영업 상태 변경 처리.
- 관리자 : 매장 등록에 필요한 은행, 카테고리, 가맹점 등을 등록, 조회, 수정, 삭제하여 관리
- 사용자 : 각 매장 조회

### 사용된 기술 및 API
- Spring JPA, Object Storage, Mysql, Thymeleaf, Javascript, toast ui editor 사용

### 설명
- 매장 사진, 사업자 등록증과 같은 대용량 파일에 대한 저장을 ObjectStorage 또는 Local에 등록하여 관리할 수 있도록 구현.
- 개발자가 ObjectStorage 외에 또 다른 저장소가 붙었을 경우를 대비하여 proxy패턴을 이용하여 구현.
- 사용자가 메인 페이지에 접근했을 때 혹은 엘라스틱서치가 매장에 대한 정보를 보여줄 때 매장의 영업 정보 및 휴무일을 기준으로 자동으로 영업상태를 변경할 수 있도록 구현.
- (영업상태는 총 4가지로, 사업자가 지정한 영업시간을 기준으로 자동으로 OPEN, BREAKTIME을 가질 수 있고, 영업 중 강제로 영업을 멈추기 위한 CLOSE상태를 추가하였습니다. 해당 상태에서 자동으로 넘어가면 안 되기 때문에 CLOSE 상태일 경우 자동 업데이트를 하지 않도록 하였습니다. 또한 영업을 영구 중단한 매장의 경우 사용자들에게 보이지 않더라도 사업자에게 중요 정보가 있을 수 있기 때문에 매장을 삭제하지 않고 단순 조회만 가능하도록 구현하였습니다.)
- 매장 및 서비스의 주요 코드들을 관리할 수 있는 관리자 페이지 구현.
- toast ui editor을 이용하여 사장님들이 다양하고 예쁘게 매장 설명할 수 있도록 구현.
- 매장 등록, 수정, 조회, 상태 변경 등 구현

---



## 검색
> 사용자가 매장에 대한 검색을 키워드를 통해 할 수 있습니다. 사업자는 매장을 검색하기 위한 동의어, 예약어를 관리할 수 있습니다.

### 담당자
- 윤동현

### 기능
- Elasticsearch을 이용한 매장 검색
- 사용자와 매장의 위치정보를 이용하여, 배달 가능 거리 (3 km 이내) 매장만을 조회
- 매장 이름, 메뉴 이름, 매장 카테고리, 지역 이름 등을 키워드로 통합하여 동적 쿼리를 생성
- Elasticsearch, Logstash, kibana 를 사용하여 데이터베이스에 있는 데이터를 통합하여 Elasticsearch 데이터를 구성
- 동의어, 예약어 사전에 대한 적용 (데이터베이스에 동의어, 예약어 테이블을 구성하여 배포 시 해당 데이터를 사전 데이터로 변환하여 적용)

### 사용기술
- Elasticsearch, Logstash, Kibana, MySQL, Spring Data Elasticsearch 사용.

### 설명
- Elasticsearch, Logstash, Kibana 를 Docker 환경으로 구성하여 서버의 구성과 배포를 자동화
- Logstash를 통해 MySQL 과의 Pipeline을 구성
- Pipeline을 통해 데이터를 받아올 때, Elasticsearch Template을 적용하여 데이터를 구성
- 데이터베이스 상에서 변경된 값을 Elasticsearch Data에 반영하는 로직과 동의어, 예약어를 반영하는 로직을 이원화
- 검색 결과에 대해 다양한 순서를 적용하여 별점 순, 거리 순, 이벤트 여부 등에 따라 다른 검색 결과를 도출
- Elasticsearch 검색 서비스를 무중단으로 제공하기 위해 3개의 노드를 클러스터로 묶어서 서버를 구성
- Nori Tokenizer를 사용하여 한글 형태소 분석 검색을 구현
- fuzzeness filter를 적용하여 오타에 대한 검색을 구현

---


## 메뉴
> 사용자가 매장의 메뉴를 간편하게 찾을 수 있습니다. 사업자는 매장에 메뉴를 간편하게 추가할 수 있습니다. 메뉴와 옵션과의 관계를 명확하게 구성하고 조회할 수 있습니다.

### 담당자
- 윤동현

### 기능
- 사업자 : 메뉴 그룹, 메뉴, 옵션 그룹, 옵션에 대한 조회, 등록, 수정 및 삭제. 본인 매장의 메뉴, 옵션 리스트 관리. 메뉴와 옵션 간의 관계 처리.
- 사용자 : 매장의 메뉴 및 옵션 조회.

### 사용된 기술 및 API
- Spring JPA, Thymeleaf, javascript, toast ui editor 사용.

### 설명
- 메뉴, 옵션의 그룹화를 통해 주문 시 원하는 메뉴에 대한 접근을 간소화
- 사업자가 각 메뉴에 대한 그룹 및 옵션을 설정할 수 있게 구현. 수정 시 메뉴에 대한 기존 정보를 불러와 수정할 수 있도록 JavaScript 파일을 구성
- 고객이 다양한 메뉴를 간편하게 고를 수 있도록 구현. 각 메뉴의 그룹에 따라 탭 방식으로 페이지에 배치
- 메뉴의 이름, 메뉴의 설명 등을 검색 조건에 포함하여 고객이 해당 메뉴의 이름을 통해 매장을 검색할 수 있도록 구현

---



## 장바구니
> 사용자가 주문하고자 하는 메뉴를 담아 편리하게 주문을 할 수 있다. 단, 한 매장에 대해서만 장바구니를 담을 수 있도록 되어 있습니다.

### 담당자
- 정제완

### 사용된 기술 및 API
- Redis, 분산락, JPA, Thymeleaf, javascript, Mysql

### 설명
- 장바구니는 회원이 많은 조회와 추가 및 수정이 발생하는 부분이라 Redis를 활용하여 바로바로 처리할 수 있도록 구현했습니다.
- 장바구니 key와 Phantom key를 두어서 장바구니 key 가 만료되어 사라지기 전에 Phantom key를 먼저 만료시켜서 Listener를 통해 캐치한 뒤 따로 키만 분리해서 Redis 장바구니 내역을 DB에 장바구니를 영구저장 하도록 구현했습니다.
   - 이렇게 사용함으로써 Redis 에 단점인 메모리 부하를 줄이고 사용자 또한 이전에 담아뒀던 장바구니 내역을 확인할 수 있습니다.
- 장바구니에 메뉴를 담아두지 않은 상태라면 빈 장바구니를 생성해서 조회 시에 빈 장바구니가 존재하면 redis 에 들어 있는 빈 key 만 인식하게 함으로써 DB 접근을 최소화하도록 구현하였습니다.
   - 만약 Redis 에 장바구니 내역이나 빈 장바구니가 존재하지 않을 시에만 DB에 접근하여 장바구니 내역을 불러와 Redis 에 다시 저장시킴으로써 DB 접근을 최소화하였습니다.
- Back API는 두 개 인스턴스로 동작하기 때문에 동시성 이슈가 발생하므로, 분산락을 활용해 먼저 들어온 장바구니를 DB에 저장하고 다음번에 들어오는 장바구니는 들어오기 전에 제약을 줘서 들어오지 못하도록 구현했습니다.

---




## 주문
> 사용자가 음식을 주문할 수 있습니다.

### 담당자
- 김주호

### 기능
- 장바구니에 담은 음식들을 주문합니다.
- 장바구니에 음식을 담지 않았을 경우, 주문 화면으로 넘어갈 수 없습니다.
- 최소 주문 금액을 충족하지 못 했을 시, 주문 화면으로 넘어갈 수 없습니다.
- 최대 주문 거리를 넘었을 시, 주문 화면으로 넘어갈 수 없습니다.
- 쿠폰, 포인트를 적용하여 총금액을 할인받을 수 있습니다.
- 주문을 받은 사장님은 주문 관리 페이지에서 해당 주문을 수락하고 배달 요청을 하거나, 혹은 취소할 수 있습니다.
- 주문 기록은 마이페이지에서 확인할 수 있습니다.

### 설명
- 주문이 생성 가능한지, 적용된 쿠폰과 포인트가 사용 가능한 지 검증합니다.
- 실제 쿠폰과 포인트는 결제되며 소비됩니다.

### 사용된 기술 및 API
- JPA, QueryDSL, Thymeleaf, JS

---




## 결제 및 환불
> 사용자가 주문한 내역들을 토스 API을 통해 간편하게 결제할 수 있다.

### 담당자
- 정제완

### 기능
- 토스 API 사용
- 토스 결제 및 취소 완료 후 데이터 저장

### 설명
- 토스 API를 활용한 결제 승인 및 취소 처리
- 토스 API를 결제 취소를 활용해 사장님 전액 및 부분환불 처리 구현

---



## 쿠폰
> 사용자가 음식을 주문할 때, 주문 금액에 대해 고정 금액 또는 퍼센트만큼 할인받아 구입할 수 있게 합니다.

### 담당자
- 김주호

### 기능
- 음식 주문 전, 매장 페이지 혹은 이벤트 페이지에서 쿠폰을 발급받습니다.
- 음식 주문 시, 쿠폰을 선택해 적용합니다.
- 쿠폰은 각각의 최소 주문 금액이 있습니다. 해당 조건을 충족하지 못 하면 주문에 적용할 수 없습니다.
- 퍼센트 쿠폰은 최대 할인 금액이 있습니다. 아무리 높은 가격이더라도 최대 할인 금액까지만 적용됩니다.
- 발급받은 쿠폰은 만료날 전까지 다시 발급받을 수 없습니다.

### 설명
- 많은 사람들이 쿠폰을 발급받는 상황을 가정, RabbitMQ를 사용하여 BE 서버의 부하를 줄였습니다.
- 같은 쿠폰을 발급받지 못 하게끔, 이벤트 발급일 경우 Redis에 쿠폰들의 코드를 Bulk Insert 하였습니다. 따라서 트랜잭션 레벨을 올리지 않고도 MQ Consumer가 빠른 속도로 쿠폰을 발급시켜 줄 수 있습니다.
- 만약 Redis의 정합성에 문제가 생겨 쿠폰 코드를 가져올 수 없을 경우, MQ Consumer가 DB에 접근해 Redis의 정합성을 다시 맞춰줄 수 있게끔 하였습니다.
- MQ Consumer가 DB에 접근하는 동안 다른 요청들을 막기 위하여 분산 락을 적용하였습니다.
- MQ Consumer가 DB에 접근하고 난 후, 밀린 요청으로 인한 미유효 쿠폰 발급 상황을 막기 위해 낙관적 락을 적용하였습니다.

### 사용된 기술 및 API
- JPA, QueryDSL, Redisson, RabbitMQ, Redis, Spring Event, Thymeleaf, JS

---



## 포인트
> 사용자가 음식을 주문할 때, 총금액에 대해 사용하는 포인트만큼 할인받아 구입할 수 있게 합니다.

### 담당자
- 김주호

### 기능
- 회원 가입, 리뷰 작성, 주문 완료 시 사용자의 포인트가 상승합니다.
- 획득한 포인트는 주문 시 현금처럼 사용할 수 있습니다.
- 사용 및 발급 포인트는 마이페이지에서 확인할 수 있습니다.

### 설명
- 회원 가입, 리뷰 작성, 주문 완료 시 포인트를 제공하기 위해 Spring Event를 구현하였습니다.
-
### 사용된 기술 및 API
- JPA, QueryDSL, Spring Event, Thymeleaf, JS

---



## 리뷰
> 사용자는 자신이 주문한 가게에 대해 리뷰를 작성할 수 있으며, 사업자는 사용자들의 리뷰에 직접 리뷰를 달 수 있습니다. 또한 리뷰를 통해 사용자들의 메뉴 및 매장 선택에 도움을 줄 수 있습니다.

### 담당자
- 유승연, 정제완, 윤동현

### 기능
- 주문완료된 건에 대해 사용자가 리뷰를 등록할 수 있고, 리뷰 등록 시 여러 장의 사진을 등록할 수 있음.
- 일반회원 :  등록한 리뷰를 조회 및 수정할 수 있음. (일반회원은 해당 리뷰를 삭제하지 못하도록 구현)
- 사업자회원 : 본인의 매장에 달린 리뷰를 확인할 수 있고, 해당 리뷰에 여러 개의 답글을 달 수 있음. (사업자는 등록, 수정, 삭제, 조회가 모두 가능함.)
- 매장 : 해당 매장에 등록된 전체 리뷰를 조회할 수 있음.

### 사용된 기술
JPA, Thymeleaf, javascript, Mysql, ObjectStorage, toast Ui editor 사용

---



## 페이지 레이아웃
> 사용자과 사업자는 웹페이지 및 모바일 화면에서 화면 크기에 맞게 재정렬된 콘텐츠를 볼 수 있습니다.

### 담당자
- 윤동현

### 기능
- 각 페이지에서 공통적으로 사용되는 CSS, JavaScript 등에 대한 레이아웃 통합
- 모바일 화면, PC 웹 화면의 너비와 높이에 맞게 페이지의 콘텐츠를 재정렬

### 사용된 기술
- Thymeleaf, Boot Strap, CSS, JavaScript

### 설명
- Thymeleaf Layout 을 적용하여 각 페이지에서 반복되는 CSS, JavaScript 주입 부분을 제거
- Boot Strap을 적용하여 html의 class 사용으로 페이지 간의 일관적인 디자인 구현
- 모바일 화면, PC 웹 화면의 너비와 높이에 맞게 페이지 콘텐츠의 너비를 줄이거나 배치를 재정렬
- 텍스트 콘텐츠의 너비가 부족한 경우에 텍스트를 아이콘으로 변경하여 사용

---



## 인프라
### 담당자
- 정제완, 윤동현, 김주호, 유승연, 추만석

### 사용된 기술
- Github Action, Jenkins, Nginx, Road Balancer, Docker, NHN Cloud Instance

### 설명
- CI/CD
   - Github Action : front, back, betch, gateway
   - Jenkins : auth, delivery
   - Github Action, Jenkins를 통해 CI를 진행하고 검증을 마친 코드들은 Docker 이미지로 생성되며 자동으로 NHN Cloud Instance에 Docker Container 가 생성되어 배포가 이뤄집니다.
- 무중단 배포
   - Front Server : 클라이언트의 요청을 Nginx 통해서 들어오고, 로드밸런서에서 Round Robin 방식으로 순서대로 Front Application에 보내지게 됩니다.
   - Back, Betch, Gateway, Delivery : Service Discovery 인 Eureka 에서 필요한 서비스가 어느 곳에 있는지에 대한 정보를 API Gateway로 반환하고 API Gateway는 이에 따라 해당 API 서비스를 호출하고 결과를 받게 됩니다.

---


## 기술
- Spring <br>
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=flat&logo=spring&logoColor=white)
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/SpringSecurity-6DB33F?style=flat&logo=springsecurity&logoColor=white)
![Spring Session](https://img.shields.io/badge/SpringSession-6DB33F?style=flat&logo=spring&logoColor=white)
![Spring Batch](https://img.shields.io/badge/SpringBatch-6DB33F?style=flat&logo=spring&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/SpringCloud-6DB33F?style=flat&logo=spring&logoColor=white)

- DB <br>
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)
![Redis](https://img.shields.io/badge/MySql-4479A1?style=flat&logo=mysql&logoColor=white)

- CI/CD <br>
![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=flat&logo=Jenkins&logoColor=white)
![Github Actions](https://img.shields.io/badge/GithubActions-2088FF?style=flat&logo=githubactions&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=Docker&logoColor=white)
![iCloud](https://img.shields.io/badge/NHNCloud-3693F3?style=flat&logo=icloud&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white)
![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=flat&logo=Ubuntu&logoColor=white)

- Search <br>
![Elastic](https://img.shields.io/badge/Elastic-005571?style=flat&logo=Elastic&logoColor=white)
![Elastic Stack](https://img.shields.io/badge/ElasticStack-005571?style=flat&logo=elasticstack&logoColor=white)
![Elastic Search](https://img.shields.io/badge/ElasticStack-005571?style=flat&logo=elasticsearch&logoColor=white)

- 그 외 기술 <br>
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=javascript&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat&logo=HTML5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=CSS3&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=CSS3&logoColor=white)
![JAVA](https://img.shields.io/badge/JAVA-007396?style=flat&logo=OpenJDK&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=flat&logo=Hibernate&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-59666C?style=flat&logo=Hibernate&logoColor=white)
![QueryDsl](https://img.shields.io/badge/QueryDsl-59666C?style=flat&logo=Hibernate&logoColor=white) <br>
![SonarLint](https://img.shields.io/badge/SonarLint-CB2029?style=flat&logo=SonarLint&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-4E9BCD?style=flat&logo=SonarQube&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=flat&logo=RabbitMQ&logoColor=white)
![ApacheMaven](https://img.shields.io/badge/ApacheMaven-C71A36?style=flat&logo=ApacheMaven&logoColor=white)
![Kakao Maps](https://img.shields.io/badge/KakaoMaps-FFCD00?style=flat&logo=Kakao&logoColor=white)
![Toss Payments](https://img.shields.io/badge/TossPayments-0085CA?style=flat&logo=contactlesspayment&logoColor=white)
