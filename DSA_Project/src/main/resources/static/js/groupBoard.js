var ajaxRequest = null;  // AJAX 요청 객체 선언

$(function() {
    var lastUrl = window.location.pathname; // 마지막으로 로드된 URL 저장 변수 초기화
    
    // 페이지가 로드되었을 때 현재 URL에 맞는 콘텐츠 로드
    loadContent(window.location.pathname);

    // 탭 클릭 시 AJAX로 콘텐츠 로드
    $('.tabs a').on('click', function() {
        var url = '/kkirikkiri' + $(this).data('url');  // context-path를 추가한 URL
        //var url = $(this).data('url');
        if (lastUrl !== url) {  // 이전 URL과 다를 경우에만 요청을 보냄
            history.pushState(null, null, url);
            loadContent(url);
        }
    });

    // 뒤로가기/앞으로가기 버튼 클릭 시 이벤트
    window.onpopstate = function() {
        var currentUrl = window.location.pathname;
        if (lastUrl !== currentUrl) {  // 이전 URL과 다를 경우에만 요청을 보냄
            loadContent(currentUrl);
        }
    };

    // AJAX로 콘텐츠를 불러오는 함수
    function loadContent(url) {
        if (lastUrl === url) { // 이전 URL과 현재 URL이 같으면 리턴 (중복 호출 방지)
            return;
        }
        
        console.log('URL에서 콘텐츠 로드 중:', url); // 호출될 때마다 로그를 출력

        // 중복 요청 방지
        if (ajaxRequest !== null) {
            ajaxRequest.abort();  // 이전 요청 중단
        }

        lastUrl = url;  // 마지막 URL을 현재 URL로 업데이트
        ajaxRequest = $.ajax({
            url: url,
            method: 'GET',
            success: function(data) {
                $('#content-box').html(data);
            },
            error: function(request, error) {  
                console.error("AJAX 요청 실패");
                console.error("상태 코드: " + request.status);
                console.error("에러 메시지: " + request.responseText);
                console.error("에러 상태: " + error);
                $('#content-box').html('<p>콘텐츠를 불러오는 중 오류가 발생했습니다.</p>');
            }
        });
    }

});
