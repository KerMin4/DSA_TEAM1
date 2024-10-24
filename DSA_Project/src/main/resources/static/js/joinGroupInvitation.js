$(function() {
    // 그룹 가입 버튼 클릭 시 AJAX로 처리
    $('#joinGroupButton').on('click', function(e) {
        e.preventDefault(); // 기본 동작 방지
        var groupId = $(this).data('group-id'); // 그룹 ID 가져오기
        
        // AJAX 요청
        $.ajax({
            type: "POST",
            url: "/kkirikkiri/socialgroup/joinGroup", // 서버로 요청할 URL
            data: { groupId: groupId }, // 서버로 전송할 데이터
            success: function(response) {
                // 성공 메시지 처리 후 소셜링 페이지로 이동
                if (response.successMessage) {
                    alert(response.successMessage); 
                    window.location.href = '/kkirikkiri/socialgroup/socialing';
                }
                // 인포 메시지 처리 후 소셜링 페이지로 이동
                else if (response.infoMessage) {
                    alert(response.infoMessage); 
                    window.location.href = '/kkirikkiri/socialgroup/socialing';
                }
                // 에러 메시지 처리 후 소셜링 페이지로 이동
                else if (response.errorMessage) {
                    alert(response.errorMessage); 
                    window.location.href = '/kkirikkiri/socialgroup/socialing';
                }
            },
            error: function(xhr) {
                // 오류 발생 시 처리
                alert("오류 발생: " + xhr.responseText); 
                window.location.href = '/kkirikkiri/socialgroup/socialing'; 
            }
        });
    });
});
