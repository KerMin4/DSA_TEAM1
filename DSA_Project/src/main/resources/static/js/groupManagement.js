document.addEventListener('DOMContentLoaded', function() {
    const dropdownContents = document.querySelectorAll('.dropdown-content');
    let activeDropdown = null;

    // 드롭다운 콘텐츠 숨김 처리
    dropdownContents.forEach(function(content) {
        content.style.display = 'none';
    });

    // 드롭다운 버튼 클릭 이벤트 처리
    const dropdownButtons = document.querySelectorAll('.dropdown-btn');
    dropdownButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            event.stopPropagation();
            const dropdownContent = button.nextElementSibling;

            // 클릭한 드롭다운 열기/닫기 처리
            if (dropdownContent.style.display === 'block') {
                dropdownContent.style.display = 'none';
                activeDropdown = null;
                console.log('Dropdown closed');
            } else {
                if (activeDropdown) {
                    activeDropdown.style.display = 'none';
                    console.log('Previous dropdown closed');
                }
                dropdownContent.style.display = 'block';
                activeDropdown = dropdownContent;
                console.log('Dropdown opened');
            }
        });
    });

    // 페이지 다른 부분 클릭 시 드롭다운 닫기
    document.addEventListener('click', function() {
        if (activeDropdown) {
            activeDropdown.style.display = 'none';
            console.log('Dropdown closed from outside click');
            activeDropdown = null;
        }
    });

    // 삭제 버튼 클릭 시 확인 대화상자 표시 및 폼 제출
    const deleteButtons = document.querySelectorAll('.delete-btn');
    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            event.stopPropagation();  // 폼 전송을 막지 않음
            const confirmed = confirm('정말로 이 그룹을 삭제하시겠습니까?');
            if (confirmed) {
                const form = button.closest('form');  // 삭제 버튼이 속한 form 찾기
                form.submit();  // 폼 제출
                console.log('그룹 삭제 요청이 서버로 전송되었습니다.');
            }
        });
    });

    // 그룹 탈퇴 버튼 클릭 시 확인 대화상자 표시
    const leaveButtons = document.querySelectorAll('.leave-btn');
    leaveButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            event.stopPropagation();
            const confirmed = confirm('정말로 이 그룹에서 탈퇴하시겠습니까?');
            if (!confirmed) {
                event.preventDefault();
            }
        });
    });

    // 그룹 카드를 클릭했을 때 해당 URL로 이동
    const groupCards = document.querySelectorAll('.group-card');
    groupCards.forEach(function(card) {
        card.addEventListener('click', function(event) {
            // 클릭이 드롭다운 버튼에서 발생한 경우에는 URL 이동 방지
            if (event.target.closest('.dropdown-btn') || event.target.closest('.dropdown-content')) {
                return;
            }
            const groupUrl = card.getAttribute('data-group-url');
            window.location.href = groupUrl;
        });
    });

    // 각 그룹 카드에서 사용자와 그룹 리더 정보 가져오기
    groupCards.forEach(function(card) {
        const currentUser = card.getAttribute('data-current-user');
        const groupLeader = card.getAttribute('data-group-leader');

        console.log('Current User:', currentUser);
        console.log('Group Leader:', groupLeader);

        // 사용자와 그룹 리더 비교
        if (String(currentUser.trim()) === String(groupLeader.trim())) {
            console.log('User is the group leader');
            card.querySelectorAll('.delete-btn').forEach(function(button) {
                button.style.display = 'block';
            });
        } else {
            console.log('User is not the group leader');
            card.querySelectorAll('.delete-btn').forEach(function(button) {
                button.style.display = 'none';
            });
        }
    });

    // 삭제 버튼의 display 상태를 모두 확인
    const deleteBtns = document.querySelectorAll('.delete-btn');
    deleteBtns.forEach(btn => {
        console.log('Final delete button display:', btn.style.display);
    });
});
