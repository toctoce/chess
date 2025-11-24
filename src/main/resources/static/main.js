const API_BASE = "/games";
let selectedTile = null; // 선택된 타일 ID (예: "A2")

const PIECE_MAP = {
    'K': '♔', 'Q': '♕', 'R': '♖', 'B': '♗', 'N': '♘', 'P': '♙',
    'k': '♚', 'q': '♛', 'r': '♜', 'b': '♝', 'n': '♞', 'p': '♟'
};

// 1. 게임 시작
async function startGame() {
    try {
        const response = await fetch(API_BASE, {method: 'POST'});
        if (!response.ok) throw new Error("게임 시작 실패");

        const gameData = await response.json();
        initBoard();
        renderGame(gameData);
        document.getElementById('undo-btn').disabled = false;
    } catch (e) {
        alert(e.message);
    }
}

// 2. 화면 렌더링
function renderGame(data) {
    document.getElementById('game-id').value = data.gameId;
    document.getElementById('game-status').innerText = `상태: ${data.status}`;
    document.getElementById('current-turn').innerText = `턴: ${data.currentTurn}`;

    const boardMap = data.board;

    // 보드 초기화 (기물 비우기)
    document.querySelectorAll('.tile').forEach(tile => tile.innerText = '');

    // 기물 배치
    for (const [position, pieceSymbol] of Object.entries(boardMap)) {
        const tile = document.getElementById(position);
        if (tile) {
            tile.innerText = PIECE_MAP[pieceSymbol] || pieceSymbol;
        }
    }
}

// 3. 타일 클릭 핸들러 (핵심 로직 수정)
async function handleTileClick(position) {
    const gameId = document.getElementById('game-id').value;
    if (!gameId) return alert("게임을 먼저 시작하세요.");

    const tile = document.getElementById(position);

    // [Case 1] 첫 번째 클릭 (기물 선택)
    if (!selectedTile) {
        // 빈 땅을 클릭하면 무시
        if (tile.innerText === '') {
            return;
        }
        selectTile(position);
        return;
    }

    // [Case 2] 두 번째 클릭 (이동 또는 취소)

    // 2-1. 같은 곳을 다시 클릭 -> 선택 취소
    if (selectedTile === position) {
        clearSelection();
        return;
    }

    // 2-2. 다른 기물(아군)을 클릭 -> 선택 변경 (선택적 UX)
    // (현재 로직에서는 이동 시도로 처리되어 'RuleViolation'이 뜨겠지만, UX 개선을 위해 추가 가능)

    // 2-3. 이동 요청
    const from = selectedTile;
    const to = position;

    // 이동 시도
    await movePiece(gameId, from, to);

    // [중요] 이동 시도 후에는 성공하든 실패하든 선택 상태를 반드시 해제해야 함!
    clearSelection();
}

// 타일 선택 함수
function selectTile(position) {
    selectedTile = position;
    const tile = document.getElementById(position);
    if (tile) {
        tile.classList.add('selected');
    }
}

// 선택 해제 함수 (문제 해결의 핵심)
function clearSelection() {
    if (selectedTile) {
        const tile = document.getElementById(selectedTile);
        if (tile) {
            tile.classList.remove('selected');
        }
    }
    selectedTile = null; // 변수 초기화 필수
}

// 이동 요청 함수
async function movePiece(gameId, from, to) {
    try {
        const response = await fetch(`${API_BASE}/${gameId}/move`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({from, to, promotion: null})
        });

        if (!response.ok) {
            const errorData = await response.json();
            // 에러 메시지 출력 (예: "자신의 턴이 아닙니다", "기물이 없습니다" 등)
            throw new Error(errorData.message || "이동 실패");
        }

        const gameData = await response.json();
        renderGame(gameData);
    } catch (e) {
        alert(e.message);
    }
    // finally 블록을 쓰지 않고 handleTileClick에서 clearSelection을 호출하도록 함
}

// 4. 무르기
async function undoMove() {
    const gameId = document.getElementById('game-id').value;
    if (!gameId) return;

    try {
        const response = await fetch(`${API_BASE}/${gameId}/undo`, {method: 'POST'});
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message);
        }
        const gameData = await response.json();
        renderGame(gameData);
        clearSelection(); // 무르기 후에도 상태 초기화
    } catch (e) {
        alert(e.message);
    }
}

// 5. 보드 생성
function initBoard() {
    const boardEl = document.getElementById('chess-board');
    boardEl.innerHTML = '';
    const files = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];

    for (let y = 7; y >= 0; y--) {
        for (let x = 0; x < 8; x++) {
            const tile = document.createElement('div');
            const position = `${files[x]}${y + 1}`;

            tile.id = position;
            tile.className = `tile ${(x + y) % 2 !== 0 ? 'white-tile' : 'black-tile'}`;
            tile.onclick = () => handleTileClick(position);

            boardEl.appendChild(tile);
        }
    }
}

initBoard();