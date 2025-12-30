let stompClient = null;

// 1. 로그인 함수: 서버의 AuthController 호출
function login() {
    const loginId = document.getElementById('loginId').value;
    const password = document.getElementById('password').value;

    const loginRequest = {
        loginId: loginId,
        password: password
    };

    fetch('http://Kt-techup-1-chat-env.eba-thmzphdi.ap-northeast-2.elasticbeanstalk.com/api/v1/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginRequest)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('로그인 실패');
            }
            return response.json();
        })
        .then(payload => { // 변수명을 헷갈리지 않게 payload로 변경했습니다.
            // 서버 응답 구조: { code: "ok", message: "성공", data: { accessToken: "...", ... } }

            // 수정된 부분: payload.data.accessToken
            if (payload.data && payload.data.accessToken) {
                const accessToken = payload.data.accessToken;

                // 토큰 저장
                localStorage.setItem('accessToken', accessToken);

                console.log("토큰 저장 완료:", accessToken); // 확인용 로그

                // UI 전환
                alert("로그인 성공!");
                document.getElementById('login-area').style.display = 'none';
                document.getElementById('chat-area').style.display = 'block';

                // 보낸 사람 이름에 아이디 자동 입력
                document.getElementById('sender').value = loginId;
            } else {
                alert("서버 응답에 토큰이 없습니다.");
                console.error("응답 데이터:", payload);
            }
        })
        .catch(error => {
            console.error('Login Error:', error);
            alert("로그인에 실패했습니다.");
        });
}

// 2. 소켓 연결
function connect() {
    const roomId = document.getElementById('roomId').value;

    // ★ 중요: 서버의 WebSocketConfig에 설정된 엔드포인트 확인
    const socket = new SockJS('http://Kt-techup-1-chat-env.eba-thmzphdi.ap-northeast-2.elasticbeanstalk.com/api/v1/ws-chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // 구독 (Subscribe) - 구독은 권한 없이 가능하다고 가정
        stompClient.subscribe('/sub/chat/room/' + roomId, function (chatMessage) {
            showGreeting(JSON.parse(chatMessage.body));
        });

        alert(roomId + "번 채팅방에 접속되었습니다.");
    }, function (error) {
        console.log('STOMP error: ' + error);
    });
}

// 3. 메시지 전송 (헤더에 토큰 포함)
function sendMessage() {
    const roomId = document.getElementById('roomId').value;
    const sender = document.getElementById('sender').value;
    const messageContent = document.getElementById('message').value;

    // 저장된 토큰 가져오기
    const token = localStorage.getItem('accessToken');

    if (!token) {
        alert("로그인이 필요합니다.");
        return;
    }

    if (messageContent && stompClient) {
        const chatMessage = {
            roomId: roomId,
            sender: sender,
            message: messageContent
        };

        // ★ 핵심: 헤더에 Authorization 추가
        const headers = {
            'Authorization': 'Bearer ' + token
        };

        // send(destination, headers, body)
        stompClient.send("/pub/chat/message", headers, JSON.stringify(chatMessage));

        document.getElementById('message').value = '';
    }
}

function showGreeting(message) {
    const chatBox = document.getElementById('chat-box');
    const msgElement = document.createElement('div');
    msgElement.className = 'msg';
    msgElement.appendChild(document.createTextNode(
        "[" + message.sender + "]: " + message.message
    ));
    chatBox.appendChild(msgElement);
    chatBox.scrollTop = chatBox.scrollHeight;
}