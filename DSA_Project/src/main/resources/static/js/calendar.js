/**
 * 
 */document.addEventListener('DOMContentLoaded', function() {
    // 캘린더 초기화
    $('#calendar').fullCalendar({
        events: groupEvents,  // 서버에서 받은 이벤트 데이터를 캘린더에 추가
        header: {
            left: 'prev,next today',
            center: 'title',
            right: 'month,agendaWeek,agendaDay'
        },
        editable: false,  // 캘린더 이벤트 수정 불가
        droppable: false  // 외부에서 드래그 앤 드롭을 허용하지 않음
    });
});
