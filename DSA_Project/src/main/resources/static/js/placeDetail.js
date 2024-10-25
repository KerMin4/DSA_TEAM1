/**
 * Place Detail
 */

 // DOMContentLoaded 이벤트를 통해 HTML이 완전히 로드된 후 실행
window.onload = function() {
    const joinButton = document.querySelector('.join-button'); // "플레이스에 참여하기" 버튼
    
    // 결제 완료 후 메시지 띄우고 placeMain으로 이동
    // 메시지가 존재하는 경우
    var message = /*[[${message}]]*/ '';
    if (message) {
        alert(message); // 알림 표시
        // 일정 시간 후 placeMain으로 이동
        setTimeout(function() {
            window.location.href = '/place/placeMain';
        }, 2000); // 2초 후 이동
    }

    // 버튼 클릭 이벤트 리스너 추가
    if (joinButton) {
        joinButton.addEventListener('click', function () {
            // 플레이스 ID를 URL에서 가져오기 (예: /place/placeDetail(placeId=123))
            const placeId = window.location.pathname.split('/').pop(); // URL의 마지막 부분에서 ID 추출

            // 서버에 참여 요청을 보내기
            fetch(`/place/joinPlace/${placeId}`, {
                method: 'POST', // 참여 요청은 POST 방식으로
                headers: {
                    'Content-Type': 'application/json' // JSON 형식으로 요청
                },
                // 필요 시 추가 데이터 전송
                body: JSON.stringify({ userId: '현재 사용자 ID' }) // 사용자 ID를 포함 (예: 로그인된 사용자)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('플레이스 참여에 실패했습니다. 상태 코드: ' + response.status);
                }
                return response.json(); // JSON 응답을 반환
            })
            .then(data => {
                // 참여 성공 시 UI 업데이트
                alert('플레이스에 성공적으로 참여하셨습니다!'); // 성공 메시지
                joinButton.disabled = true; // 버튼 비활성화
                joinButton.textContent = '참여 완료'; // 버튼 텍스트 변경
            })
            .catch(error => {
                console.error('문제가 발생했습니다:', error);
                alert('오류가 발생했습니다: ' + error.message); // 에러 메시지 표시
            });
        });
    }
    
};
