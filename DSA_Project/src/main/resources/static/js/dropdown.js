document.addEventListener("DOMContentLoaded", function() {
    const profileImg = document.getElementById("profileImg");
    const profileDropdown = document.getElementById("profileDropdown");
	
    profileImg.addEventListener("click", function(event) {
 
        profileDropdown.classList.toggle("show"); 
    });

 /*
 	 notificationBtn.addEventListener("mouseenter", function () {
        notificationDropdown.classList.add("show");
        loadNotifications(); // 알림 데이터 로드
    });

	notificationDropdown.addEventListener("mouseleave", function () {
        notificationDropdown.classList.remove("show");
    });
*/         
    const notificationBtn = document.getElementById('notification-btn');
    const dropdown = document.getElementById('notification-dropdown');
    
     notificationBtn.addEventListener("click", function(event) {
		 event.stopPropagation();
        dropdown.classList.toggle("show"); 
    });
    

    notificationBtn.addEventListener('click', () => {
        const userId = notificationBtn.getAttribute('data-user-id');
        console.log("됨?");
        // 서버에서 알림 데이터를 가져오기
        fetch(`/notifications/api?userId=${userId}`)
            .then(response => response.json())
            .then(notifications => {
                dropdown.innerHTML = ''; // 기존 목록 초기화

                if (notifications.length > 0) {
                    notifications.forEach(notification => {
                        const li = document.createElement('li');
                        li.textContent = notification.message; // 알림 메시지 표시
                        dropdown.appendChild(li);
                    });
                } else {
                    dropdown.innerHTML = '<li>새로운 알림이 없습니다.</li>';
                }

                dropdown.style.display = 'block'; // 드롭다운 표시
            })
            .catch(error => console.error('알림 로드 오류:', error));
    });


    document.addEventListener("click", function(event) {
        if (!profileDropdown.contains(event.target) && !profileImg.contains(event.target)) {
            profileDropdown.classList.remove("show"); 
        }
        if (!notificationBtn.contains(event.target) && !dropdown.contains(event.target)) {
            dropdown.style.display = 'none';
        }
    });
});
