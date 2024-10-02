$(function() {
	// 카테고리 목록 토글
    $('#toggleCategoryBtn').on('click', function() {
        $('#category-container').toggle(); // 카테고리 목록을 토글
        var isVisible = $('#category-container').is(':visible');
        $(this).text(isVisible ? '카테고리 숨기기' : '카테고리 보기'); // 버튼 텍스트 변경
    });
	
	// 해시태그 데이터를 서버로부터 불러옴
    $.ajax({
        url: '/api/hashtags',  // 해시태그 데이터를 가져올 API 엔드포인트
        type: 'GET',
        dataType: 'json',  // 서버로부터 받을 데이터 유형을 명시
        beforeSend: function() {
            // 데이터를 불러오는 동안 로딩 메시지를 표시
            $('#hashtag-container').html('<p>Loading hashtags...</p>');
        },
        success: function(response) {
            var hashtags = response.hashtags; // 서버에서 받은 해시태그 목록
            var hashtagContainer = $('#hashtag-container');
            
            // 이전 로딩 메시지를 지움
            hashtagContainer.empty();

            // 서버에서 해시태그가 없을 경우
            if (hashtags.length === 0) {
                hashtagContainer.html('<p>No hashtags available</p>');
                return;
            }

            // 해시태그 리스트를 동적으로 생성
            hashtags.forEach(function(tag) {
                var button = $('<button></button>')
                    .text('#' + tag.name)  // 해시태그 앞에 # 붙임
                    .addClass('hashtag-button');  // 클래스 추가 (스타일 적용을 위해)
                hashtagContainer.append(button); // 해시태그 버튼을 추가
            });
        },
        error: function(err) {
            console.error("해시태그 데이터를 불러오지 못했습니다:", err);
            $('#hashtag-container').html('<p>Failed to load hashtags. Please try again later.</p>');
        }
    });
    
});