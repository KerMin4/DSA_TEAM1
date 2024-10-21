$(function() {
    $('#editButton').on('click', function() {
		var currentLocation = $('#locationDisplay').text();
        var currentEventDate = $('#eventDateDisplay').text();
        
        $('#eventDateInput').val(currentEventDate);
        $('#locationInput').val(currentLocation);
		
        $('#eventDateDisplay').hide();
        $('#locationDisplay').hide();
        $('#eventDateInput').show();
        $('#locationInput').show();
        $(this).hide();
        $('#saveButton').show();
    });

    $('#saveButton').on('click', function() {
		event.preventDefault();
		
        var eventDate = $('#eventDateInput').val();
        var location = $('#locationInput').val();
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
                $('#locationInput').hide();
                $('#saveButton').hide();
                $('#editButton').show();
            },
            error: function(xhr) {
                alert('일정 업데이트에 실패했습니다.');
            }
        });
    });

});