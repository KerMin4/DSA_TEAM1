$(function() {
    var groupId = $('#groupId').val(); // groupId 값을 가져옴

    $('.tabs a').on('click', function(event) {
        event.preventDefault(); // 기본 동작 중지
        var url = $(this).data('url'); // data-url에서 URL 가져오기

        if (!url.includes('groupId')) {
            url += '?groupId=' + groupId;  // groupId가 없으면 추가
        }

        console.log(url); // 여기에서 url이 올바른지 확인
        history.pushState(null, null, url); // URL 변경 (브라우저 히스토리에 추가)
        
        // AJAX로 콘텐츠 로드
        loadGroupBoardContent(url);
    });


    // 그룹 보드 관련 함수
	function loadGroupBoardContent(url) {
	    $.ajax({
	        url: url,
	        method: 'GET',
	        success: function(data) {
	            // 처음에는 전체 응답 데이터를 content-box에 삽입
                $('#content-box').html(data);  // 전체 데이터를 넣어서 제대로 출력되는지 확인
	        },
	        error: function(xhr, status, error) {
	            console.error("AJAX 요청 실패");
	            console.error("상태 코드: " + xhr.status); // 상태 코드 확인
	            console.error("에러 메시지: " + xhr.responseText); // 에러 메시지 확인
	        }
	    });
	}
});
