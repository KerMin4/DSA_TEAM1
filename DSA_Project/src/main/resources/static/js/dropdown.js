document.addEventListener("DOMContentLoaded", function() {
    const profileImg = document.getElementById("profileImg");
    const profileDropdown = document.getElementById("profileDropdown");

    // 프로필 이미지를 클릭하면 드롭다운을 토글
    profileImg.addEventListener("click", function(event) {
        event.stopPropagation(); // 클릭 이벤트가 다른 요소에 전파되지 않도록 방지
        profileDropdown.classList.toggle("show"); // 드롭다운 메뉴 표시/숨김
    });

    // 외부를 클릭하면 드롭다운을 닫기
    window.onclick = function(event) {
        if (!event.target.matches('#profileImg') && !profileDropdown.contains(event.target)) {
            if (profileDropdown.classList.contains('show')) {
                profileDropdown.classList.remove('show'); // 드롭다운 메뉴 숨김
            }
        }
    };
});
