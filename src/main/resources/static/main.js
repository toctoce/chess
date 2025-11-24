const API_BASE = "/games";
let selectedTile = null; // 선택된 타일 ID (예: "A2")
let pollingInterval = null; // [수정 1] 폴링 인터벌 변수 선언

const PIECE_MAP = {
    'K': '♔', 'Q': '♕', 'R': '♖', 'B': '♗', 'N': '♘', 'P': '♙',
    'k': '♚', 'q': '♛', 'r': '♜', 'b': '♝', 'n': '♞', 'p': '♟'
};

// 서버 에러 메시지 추출 헬퍼 함수
async function getErrorMessage(response) {
    try {
        const errorData = await response.json();
        return errorData.message || `알 수 없는 오류 (${response.status})`;
    } catch (e) {
        return `서버 통신 오류 (${response.status} ${response.statusText})`;
    }
}

// 1. 게임 시작
async function startGame() {
    try {
        const response = await fetch(API_BASE, {method: 'POST'});

        if (!response.ok) {
            throw new Error(await getErrorMessage(response));
        }

        const gameData = await response.json();
        initBoard();
        renderGame(gameData);
        document.getElementById('undo-btn').disabled = false;

        startPolling(gameData.gameId);
    } catch (e) {
        alert(e.message);
    }
}

// [신규] 1초마다 서버 상태 확인 (Polling)
function startPolling(gameId) {
    if (pollingInterval) clearInterval(pollingInterval);

    pollingInterval = setInterval(async () => {
        try {
            const response = await fetch(`${API_BASE}/${gameId}`);
            if (response.ok) {
                const gameData = await response.json();
                renderGame(gameData);
            }
        } catch (e) {
            console.error("Polling error:", e);
        }
    }, 1000);
}

// 2. 화면 렌더링
function renderGame(data) {
    document.getElementById('game-id').value = data.gameId;
    document.getElementById('game-status').innerText = `상태: ${data.status}`;
    document.getElementById('current-turn').innerText = `턴: ${data.currentTurn}`;

    const boardMap = data.board;

    document.querySelectorAll('.tile').forEach(tile => tile.innerText = '');

    for (const [position, pieceSymbol] of Object.entries(boardMap)) {
        const tile = document.getElementById(position);
        if (tile) {
            tile.innerText = PIECE_MAP[pieceSymbol] || pieceSymbol;
        }
    }
}

// 3. 타일 클릭 핸들러
async function handleTileClick(position) {
    const gameId = document.getElementById('game-id').value;
    if (!gameId) return alert("게임을 먼저 시작하세요.");

    const tile = document.getElementById(position);

    if (!selectedTile) {
        if (tile.innerText === '') return;
        selectTile(position);
        return;
    }

    if (selectedTile === position) {
        clearSelection();
        return;
    }

    await movePiece(gameId, selectedTile, position);
    clearSelection();
}

function selectTile(position) {
    selectedTile = position;
    const tile = document.getElementById(position);
    if (tile) tile.classList.add('selected');
}

function clearSelection() {
    if (selectedTile) {
        const tile = document.getElementById(selectedTile);
        if (tile) tile.classList.remove('selected');
    }
    selectedTile = null;
}

async function movePiece(gameId, from, to) {
    try {
        const response = await fetch(`${API_BASE}/${gameId}/move`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({from, to, promotion: null})
        });

        if (!response.ok) {
            throw new Error(await getErrorMessage(response));
        }

        const gameData = await response.json();
        renderGame(gameData);
    } catch (e) {
        alert(e.message);
    }
}

// 4. 무르기
async function undoMove() {
    const gameId = document.getElementById('game-id').value;
    if (!gameId) return;

    try {
        const response = await fetch(`${API_BASE}/${gameId}/undo`, {method: 'POST'});

        if (!response.ok) {
            throw new Error(await getErrorMessage(response));
        }

        const gameData = await response.json();
        renderGame(gameData);
        clearSelection();
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

// 게임 참여 함수
async function joinGame() {
    const gameId = document.getElementById('join-game-id').value;
    if (!gameId) {
        alert("게임 ID를 입력해주세요.");
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/${gameId}/join`, {method: 'POST'});

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "게임 참여 실패");
        }

        const gameData = await response.json();
        initBoard();
        renderGame(gameData);
        document.getElementById('undo-btn').disabled = false;
        alert(`게임 ${gameData.gameId}번에 참여했습니다! 당신은 흑(Black)입니다.`);

        // [수정 2] 게임 참여 시에도 폴링 시작
        startPolling(gameData.gameId);

    } catch (e) {
        alert(e.message);
    }
}

initBoard();