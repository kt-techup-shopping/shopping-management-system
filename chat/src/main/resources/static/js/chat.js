let stompClient = null;

// ===== cursor & paging 상태 =====
let lastCreatedAt = null;
let isLoading = false;
let hasMore = true;

// 스크롤 로딩 기준선
let loadTriggerTop = null;

// ===== API BASE =====
// const API_BASE = "http://kt-techup-1-chat-env.eba-thmzphdi.ap-northeast-2.elasticbeanstalk.com";
const API_BASE = "http://localhost:8080";


// ==========================
// 로그인
// ==========================
function login() {
    const loginId = document.getElementById('loginId').value;
    const password = document.getElementById('password').value;

    fetch(`${API_BASE}/api/v1/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ loginId, password })
    })
        .then(res => {
            if (!res.ok) throw new Error("로그인 실패");
            return res.json();
        })
        .then(payload => {
            const token = payload.data?.accessToken;
            if (!token) throw new Error("토큰 없음");

            localStorage.setItem("accessToken", token);

            alert("로그인 성공");
            document.getElementById('login-area').style.display = 'none';
            document.getElementById('chat-area').style.display = 'block';
        })
        .catch(err => {
            console.error(err);
            alert("로그인 실패");
        });
}

// ==========================
// 채팅방 연결
// ==========================
function connect() {
    const roomId = document.getElementById('roomId').value;

    resetChatState();

    // 최신 20개 로드
    loadPreviousChats(roomId, true);

    //⃣ WebSocket 연결
    const socket = new SockJS(`${API_BASE}/api/v1/ws-chat`);
    stompClient = Stomp.over(socket);

    // 저장된 액세스 토큰 가져오기
    const token = localStorage.getItem('accessToken');

    // 헤더 객체 생성
    const headers = {
        'Authorization': 'Bearer ' + token
    };

    // connect의 첫 번째 인자로 headers 전달
    stompClient.connect(headers, function () {
        stompClient.subscribe(`/sub/chat/room/${roomId}`, function (message) {
            appendMessage(JSON.parse(message.body));
        });

        console.log(`${roomId}번 채팅방 연결 완료`);
    }, function (error) {
        // (선택) 연결 실패 시 로그 확인용
        console.error("STOMP 연결 에러:", error);
    });
}

// ==========================
// 과거 채팅 조회 (REST)
// ==========================
function loadPreviousChats(roomId, isFirst = false) {
    if (isLoading || !hasMore) return;
    isLoading = true;

    let url = `${API_BASE}/api/v1/chats/rooms/${roomId}`;
    if (!isFirst && lastCreatedAt) {
        url += `?lastCreatedAt=${encodeURIComponent(lastCreatedAt)}`;
    }

    fetch(url)
        .then(res => res.json())
        .then(response => {
            const chats = response.data;
            if (!chats || chats.length === 0) {
                hasMore = false;
                return;
            }

            // prepend 시 스크롤 유지
            const chatBox = document.getElementById('chat-box');
            const prevHeight = chatBox.scrollHeight;

            // 서버: 최신 → 과거
            chats
                .slice()
                // .reverse() // 과거 → 최신
                .forEach(chat => prependMessage(chat));

            const newHeight = chatBox.scrollHeight;
            chatBox.scrollTop += (newHeight - prevHeight);

            // 커서는 "가장 오래된 메시지"
            const oldest = chats[chats.length - 1];
            lastCreatedAt = oldest.createdAt;

            // 🔥 다음 로딩 기준선 재설정
            loadTriggerTop = chatBox.scrollHeight * 0.4;

            if (isFirst) {
                scrollToBottom();
            }
        })
        .finally(() => {
            isLoading = false;
        });
}

function scrollToBottom() {
    const chatBox = document.getElementById('chat-box');
    chatBox.scrollTop = chatBox.scrollHeight;
}

// ==========================
// 메시지 전송 (WS)
// ==========================
function sendMessage() {
    const roomId = document.getElementById('roomId').value;
    const message = document.getElementById('message').value;
    const token = localStorage.getItem("accessToken");

    if (!token) {
        alert("로그인이 필요합니다.");
        return;
    }

    if (!message || !stompClient) return;

    stompClient.send(
        "/pub/chat/message",
        { Authorization: `Bearer ${token}` },
        JSON.stringify({ roomId, message })
    );

    document.getElementById('message').value = '';
}

// ==========================
// 메시지 렌더링
// ==========================
function appendMessage(msg) {
    const chatBox = document.getElementById('chat-box');
    const div = document.createElement('div');
    div.className = 'msg';
    div.innerText = `[${msg.senderId}] ${msg.message}`;

    chatBox.appendChild(div);
    chatBox.scrollTop = chatBox.scrollHeight;
}

function prependMessage(msg) {
    const chatBox = document.getElementById('chat-box');
    const div = document.createElement('div');
    div.className = 'msg';
    div.innerText = `[${msg.senderId}] ${msg.message}`;

    chatBox.insertBefore(div, chatBox.firstChild);
}

// ==========================
// 스크롤 이벤트 (무한 스크롤)
// ==========================
document.getElementById('chat-box').addEventListener('scroll', function () {
    if (isLoading || !hasMore) return;

    // 🔥 기준선 통과 시만 호출
    if (this.scrollTop < loadTriggerTop) {
        const roomId = document.getElementById('roomId').value;
        loadPreviousChats(roomId);
    }
});

// ==========================
// 상태 초기화
// ==========================
function resetChatState() {
    document.getElementById('chat-box').innerHTML = '';
    lastCreatedAt = null;
    isLoading = false;
    hasMore = true;
    loadTriggerTop = null;
}
