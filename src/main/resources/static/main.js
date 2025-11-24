const API_BASE = "/games";
let selectedTile = null;
let stompClient = null;

const PIECE_MAP = {
    'K': '♔', 'Q': '♕', 'R': '♖', 'B': '♗', 'N': '♘', 'P': '♙',
    'k': '♚', 'q': '♛', 'r': '♜', 'b': '♝', 'n': '♞', 'p': '♟'
};

// 에러 메시지 헬퍼
async function getErrorMessage(response) {
    try {
        const errorData = await response.json();
        return errorData.message || `알 수 없는 오류 (${response.status})`;
    } catch (e) {
        return `서버 통신 오류 (${response.status})`;
    }
}

// 1. 게임 시작 (White)
async function startGame() {
    try {
        const response = await fetch(API_BASE, {method: 'POST'});
        if (!response.ok) throw new Error(await getErrorMessage(response));

        const gameData = await response.json();

        // White 설정
        document.getElementById('my-role').value = 'WHITE';
        setupGameScreen(gameData, false); // false = 뒤집지 않음

        connectWebSocket(gameData.gameId);
    } catch (e) {
        alert(e.message);
    }
}

// 2. 게임 참여 (Black)
async function joinGame() {
    const gameId = document.getElementById('join-game-id').value;
    if (!gameId) return alert("게임 ID를 입력해주세요.");

    try {
        const response = await fetch(`${API_BASE}/${gameId}/join`, {method: 'POST'});
        if (!response.ok) throw new Error(await getErrorMessage(response));

        const gameData = await response.json();

        // Black 설정
        document.getElementById('my-role').value = 'BLACK';
        setupGameScreen(gameData, true); // true = 보드 뒤집기!

        connectWebSocket(gameData.gameId);
        alert(`게임 ${gameData.gameId}번에 참여했습니다! 당신은 흑입니다.`);
    } catch (e) {
        alert(e.message);
    }
}

// [신규] 게임 화면 세팅 및 전환
function setupGameScreen(gameData, isFlipped) {
    // 화면 전환
    document.getElementById('start-screen').classList.add('hidden');
    document.getElementById('game-screen').classList.remove('hidden');

    // 보드 초기화
    initBoard(isFlipped);
    renderGame(gameData);
}

// 3. 웹소켓 연결
function connectWebSocket(gameId) {
    if (stompClient && stompClient.connected) return;

    const socket = new SockJS('/ws-chess');
    stompClient = Stomp.over(socket);
    // stompClient.debug = null; // 로그 끄기

    stompClient.connect({}, function (frame) {
        stompClient.subscribe(`/topic/games/${gameId}`, function (message) {
            const gameData = JSON.parse(message.body);
            renderGame(gameData);
        });
    }, function (error) {
        console.error("WebSocket Error:", error);
    });
}

// 4. 화면 렌더링 (게임 정보 업데이트)
function renderGame(data) {
    // 게임 정보 업데이트
    document.getElementById('display-game-id').innerText = data.gameId;
    document.getElementById('game-status').innerText = `상태: ${data.status}`;

    const turnBox = document.getElementById('current-turn-box');
    turnBox.className = 'turn-box'; // 기본 클래스로 리셋

    if (data.currentTurn === 'WHITE') {
        turnBox.classList.add('white');
    } else {
        turnBox.classList.add('black');
    }
    // 턴 색상에 따라 텍스트 색상 변경 (시각적 효과)

    const boardMap = data.board;
    document.querySelectorAll('.tile').forEach(tile => tile.innerText = '');

    for (const [position, pieceSymbol] of Object.entries(boardMap)) {
        const tile = document.getElementById(position);
        if (tile) tile.innerText = PIECE_MAP[pieceSymbol] || pieceSymbol;
    }
}

// 5. 타일 클릭 핸들러
async function handleTileClick(position) {
    const tile = document.getElementById(position);

    // 내 턴이 아니거나, 내 기물이 아니면 클릭 막기 (UX 개선)
    // (엄격한 검증은 서버가 하지만, 프론트에서도 막아두면 좋음)
    // 여기서는 간단히 선택 로직만 수행

    if (!selectedTile) {
        if (tile.innerText === '') return;
        selectTile(position);
        return;
    }

    if (selectedTile === position) {
        clearSelection();
        return;
    }

    // 이동 요청
    const gameId = document.getElementById('display-game-id').innerText;
    await movePiece(gameId, selectedTile, position);
    clearSelection();
}

function selectTile(position) {
    selectedTile = position;
    document.getElementById(position)?.classList.add('selected');
}

function clearSelection() {
    if (selectedTile) {
        document.getElementById(selectedTile)?.classList.remove('selected');
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

        if (!response.ok) throw new Error(await getErrorMessage(response));
    } catch (e) {
        alert(e.message);
    }
}

async function undoMove() {
    const gameId = document.getElementById('display-game-id').innerText;
    if (!gameId) return;

    try {
        const response = await fetch(`${API_BASE}/${gameId}/undo`, {method: 'POST'});
        if (!response.ok) throw new Error(await getErrorMessage(response));
        clearSelection();
    } catch (e) {
        alert(e.message);
    }
}

// 6. 보드 생성 (뒤집기 지원)
function initBoard(isFlipped) {
    const boardEl = document.getElementById('chess-board');
    boardEl.innerHTML = '';

    // 보드 자체 뒤집기 (CSS transform 사용)
    if (isFlipped) {
        boardEl.classList.add('flipped');
    } else {
        boardEl.classList.remove('flipped');
    }

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