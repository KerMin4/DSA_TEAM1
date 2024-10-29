$(function() {
    // 그룹 리더가 아닌 경우 수정 버튼 숨기기
    if (currentUserId !== groupLeaderId) {
        $('#editButton').hide(); // 리더가 아닌 경우 수정 버튼 숨김
    }

    $('#editButton').on('click', function() {
        var currentLocation = $('#locationDisplay').text();
        var currentEventDate = $('#eventDateDisplay').text();
        
        $('#eventDateInput').val(currentEventDate);
        $('#location').val(currentLocation);
		
        $('#eventDateDisplay').hide();
        $('#locationDisplay').hide();
        $('#eventDateInput').show();
        $('#findPlace').show();
        $('#location').show();
        $(this).hide();
        $('#saveButton').show();
    });
    
    // 지역 찾기 버튼
    $('#findPlace').click(function() {
        console.log('지역 찾기 버튼 클릭됨');
        window.open('/kkirikkiri/member/mapTest', '지역찾기', 'fullscreen');
        
        const location = $('#location').val();
        console.log('입력된 위치 값:', location);

        $('#locationForm').submit();
    });
    
    $('#saveButton').on('click', function() {
		event.preventDefault();
		
        var eventDate = $('#eventDateInput').val();
        var location = $('#location').val();
        var groupId = $('#groupId').val();
        
        // 오전/오후 형식을 변환해서 사용자가 볼 수 있게 처리
	    var parsedDate = new Date(eventDate);
	    var formattedDate = parsedDate.toLocaleString('ko-KR', {
	        year: 'numeric',
	        month: '2-digit',
	        day: '2-digit',
	        hour: '2-digit',
	        minute: '2-digit',
	        hour12: true
	    });

        $.ajax({
            url: '/kkirikkiri/groupboard/schedule/update',
            type: 'POST',
            data: {
                groupId: groupId,
                eventDate: eventDate,
                location: location
            },
            success: function(response) {
                alert('일정이 성공적으로 업데이트되었습니다.');
                $('#eventDateDisplay').text(formattedDate).show();  // 업데이트된 시간 표시
                $('#locationDisplay').text(location).show();
                $('#eventDateInput').hide();
                $('#findPlace').hide();
                $('#location').hide();
                $('#saveButton').hide();
                $('#editButton').show();
            },
            error: function(xhr) {
                alert('일정 업데이트에 실패했습니다.');
            }
        });
    });
});
