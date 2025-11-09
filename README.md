# 체스
체스 게임을 구현한다.
## 과제 목표
1. 객체지향적 설계 
    - 3주차까지 과제를 진행하면서 점점 객체지향적인 설계에 익숙해지고 있다는 생각이 들었으며, 동시에 이 역량을 더 기르고 싶다는 생각도 들었다. 따라서 객체지향적 설계를 1번 목표로 삼았다.
2. 디자인 패턴
   - 코드 리뷰를 하며 디자인 패턴에 대해서 감을 잡았다. 디자인 패턴 중 몇 가지를 직접 공부하고 적용해보고 싶었다. 따라서 디자인 패턴을 2번 목표로 삼았다.
3. 배포
   - 아직 배포를 해본 경험이 없으며, 프로젝트를 한다면 꼭 배포를 해서 온라인상에서 내가 구현한 프로그램에 접근해보고 싶었다. 따라서 배포를 3번 목표로 삼았다.
## 용어
- 주기물 : 룩, 퀸을 묶어서 통칭
  - 킹과 주기물 1개로 체크메이트를 강제할 수 있다.
- 부기물 : 비숍, 나이트를 묶어서 통칭
  - 킹과 부기물 1개로 체크메이트를 강제할 수 없다.
## 기능 요구사항
- 체스 기본 룰을 따른다.
- 조건에 부합하면 캐슬링이 가능하다.
  - 캐슬링 : 킹을 킹/퀸사이드로 2칸 이동한 후, 룩을 킹의 반대편으로 이동하는 규칙
  - 조건 : 킹과 룩 사이에 다른 기물이 없고, 두 기물 모두 움직인 적이 없으며, 킹과 킹이 이동하는 길목이 공격받고 있지 않아야 한다.
- 폰의 고유의 이동 규칙을 따른다.
  - 전진만 가능
  - 첫 회 한정으로 2칸 이동 가능
  - 공격 시 대각선으로만 가능
  - 승진 : 마지막 행에 도달할 시 다른 기물 중 하나로 바뀜 
  - 앙파상 : 상대 폰이 두 칸 전진하여 내 폰 옆으로 이동했을 때, 마치 한 칸만 움직인 것처럼 대각선으로 공격 가능
- 경기가 끝나는 경우는 다음과 같다.
  - 승리/패배
    - 체크메이트
    - 기권
  - 무승부
    - 기물 부족 : 가지고 있는 기물이 체크메이트를 강제할 수 없는 경우
      - 킹 vs 킹
      - 킹 vs 킹 + 부기물
      - 킹 vs 모든 기물
      - 킹 vs 킹 + 2 나이트
      - 킹 + 부기물 vs 킹 + 부기물
    - 스테일메이트 : 자신의 차례에 둘 수 있는 수가 아무 것도 없는 경우
    - 50수 규칙 : 50수 동안 폰의 움직임 또는 상대 기물을 잡는 수가 두어지지 않는 경우
      - 단, 폰의 승진도 폰의 움직임이다.
    - 반복 : 같은 포지션이 3회 반복되는 경우
    - 합의
- 사용자가 잘못된 값을 입력할 경우 IllegalArgumentException을 발생시키고, "[ERROR]"로 시작하는 에러 메시지를 출력 후 그 부분부터 입력을 다시 받는다.
## 예외 케이스
- 본인의 기물이 아닌 기물을 이동
- 이동 시작 위치에 기물이 없음
- 불가능한 위치로 이동
  - 보드 밖으로 이동
  - 이동 위치에 본인의 말 존재
- 폰의 비정상적인 이동
  - 첫 움직임이 아닌데 두 칸 이상 이동
  - 공격 시 대각선이 아닌 경로로 이동
- 체크메이트 당하는 수를 두는 경우
## 입출력 요구사항
### 예시
## 시퀀스 다이어그램
## 구현할 기능
## 프로젝트 구조
추상팩토리패턴, 전략패턴, 상태패턴, 커맨드패턴을 적용할 것이다.
```text
chess
├── controller
│   └── ChessController.java          
├── service
│   └── GameService.java              
└── domain
    ├── board
    │   ├── Board.java
    │   └── Position.java
    ├── piece
    │   ├── Piece.java
    │   ├── Color.java
    │   └── impls
    │       ├── King.java
    │       ├── Queen.java
    │       ├── Rook.java
    │       ├── Bishop.java
    │       ├── Knight.java
    │       └── Pawn.java
    ├── factory // 추상팩토리패턴
    │   ├── PieceFactory.java
    │   └── impls
    │       ├── BlackPieceFactory.java
    │       └── WhitePieceFactory.java
    ├── strategy // 전략패턴
    │   ├── MovementStrategy.java
    │   └── impls
    │       ├── KingMovement.java
    │       ├── QueenMovement.java
    │       ├── RookMovement.java
    │       ├── BishopMovement.java
    │       ├── KnightMovement.java
    │       └── PawnMovement.java
    ├── state // 상태패턴
    │   ├── GameState.java
    │   └── impls
    │       ├── OngoingState.java
    │       ├── CheckmateState.java
    │       └── StalemateState.java
    ├── command // 커맨트패턴
    │   ├── MoveCommand.java
    │   └── impls
    │       └── BasicMoveCommand.java
    └── rule
        ├── CheckDetector.java
        └── RuleValidator.java
```