$(function() {
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
    
    // 지역찾기 버튼
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
                $('#eventDateDisplay').text(eventDate).show();
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