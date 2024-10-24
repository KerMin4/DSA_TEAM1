/*const eventSource = new EventSource('/notify');
    	
    	eventSource.onmessage = function(event){
    		const badge = document.getElementById('notification-badge');
            badge.style.display = 'inline'; 
    	};
    	
    	  eventSource.onerror = function () {
              console.error('알림 연결이 끊어졌습니다.');
              eventSource.close(); // 연결 실패 시 이벤트 소스 닫기
          };*/
          
window.addEventListener('DOMContentLoaded', () => {
    fetch('/notifications/check')
        .then(response => response.json())
        .then(hasUnread => {
            const badge = document.getElementById('notification-badge');
            if (hasUnread) {
                badge.style.display = 'inline';
            }
        })
        .catch(error => console.error('알림 체크 오류:', error));
});
/*
document.getElementById('notification-btn').addEventListener('click', () => {
    const userId = document.getElementById('notification-btn').getAttribute('data-user-id');
    console.log('알림 버튼 클릭됨:', userId);

    fetch(`/notifications/markAsRead?userId=${userId}`, { method: 'POST' })
        .then(response => {
            if (response.ok) {
                document.getElementById('notification-badge').style.display = 'none';
            } else {
                console.error('알림 읽음 처리 실패');
            }
        })
        .catch(error => console.error('에러 발생:', error));
});*/