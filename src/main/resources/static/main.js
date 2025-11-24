const API_BASE = "/games";
let selectedTile = null;
let stompClient = null; // 웹소켓 클라이언트

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

// 1. 게임 시작
async function startGame() {
    try {
        const response = await fetch(API_BASE, {method: 'POST'});
        if (!response.ok) throw new Error(await getErrorMessage(response));

        const gameData = await response.json();
        initBoard();
        renderGame(gameData);
        document.getElementById('undo-btn').disabled = false;

        // [변경] 폴링 대신 웹소켓 연결
        connectWebSocket(gameData.gameId);

    } catch (e) {
        alert(e.message);
    }
}

// 2. 게임 참여
async function joinGame() {
    const gameId = document.getElementById('join-game-id').value;
    if (!gameId) return alert("게임 ID를 입력해주세요.");

    try {
        const response = await fetch(`${API_BASE}/${gameId}/join`, {method: 'POST'});
        if (!response.ok) throw new Error(await getErrorMessage(response));

        const gameData = await response.json();
        initBoard();
        renderGame(gameData);
        document.getElementById('undo-btn').disabled = false;
        alert(`게임 ${gameData.gameId}번에 참여했습니다!`);

        // [변경] 폴링 대신 웹소켓 연결
        connectWebSocket(gameData.gameId);

    } catch (e) {
        alert(e.message);
    }
}

// [신규] 웹소켓 연결 및 구독 함수
function connectWebSocket(gameId) {
    if (stompClient && stompClient.connected) {
        return; // 이미 연결됨
    }

    const socket = new SockJS('/ws-chess'); // WebSocketConfig에서 설정한 엔드포인트
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // 서버가 /topic/games/{gameId} 로 메시지를 보내면 여기서 받음
        stompClient.subscribe(`/topic/games/${gameId}`, function (message) {
            const gameData = JSON.parse(message.body);
            renderGame(gameData); // 받은 데이터로 화면 즉시 갱신
        });
    }, function (error) {
        console.error("WebSocket Error:", error);
    });
}

// 3. 화면 렌더링
function renderGame(data) {
    document.getElementById('game-id').value = data.gameId;
    document.getElementById('game-status').innerText = `상태: ${data.status}`;
    document.getElementById('current-turn').innerText = `턴: ${data.currentTurn}`;

    const boardMap = data.board;
    document.querySelectorAll('.tile').forEach(tile => tile.innerText = '');

    for (const [position, pieceSymbol] of Object.entries(boardMap)) {
        const tile = document.getElementById(position);
        if (tile) tile.innerText = PIECE_MAP[pieceSymbol] || pieceSymbol;
    }
}

// 4. 타일 클릭 & 이동
async function handleTileClick(position) {
    const gameId = document.getElementById('game-id').value;
    if (!gameId) return alert("게임을 시작하세요.");

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

        // [중요] 여기서 renderGame을 호출할 필요가 없음.
        // 서버가 웹소켓으로 보낸 메시지를 받아서 renderGame이 자동 호출될 것이기 때문.
        // 하지만 네트워크 딜레이 등으로 어색할 수 있으니 놔둬도 무방함.

    } catch (e) {
        alert(e.message);
    }
}

// 5. 무르기
async function undoMove() {
    const gameId = document.getElementById('game-id').value;
    if (!gameId) return;

    try {
        const response = await fetch(`${API_BASE}/${gameId}/undo`, {method: 'POST'});
        if (!response.ok) throw new Error(await getErrorMessage(response));
        clearSelection();
    } catch (e) {
        alert(e.message);
    }
}

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