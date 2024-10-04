$(document).ready(function() {
    var calendarEl = document.getElementById('calendar');
    
    // FullCalendar 인스턴스 생성 및 설정
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',  // 초기 뷰는 월 단위
        events: [
            {
                title: '취미 모임',
                start: '2024-10-10',
                description: '취미 관련 모임',
            },
            {
                title: '자기계발 세미나',
                start: '2024-10-12',
                description: '자기계발을 위한 세미나',
            },
            {
                title: '게임 대회',
                start: '2024-10-15',
                description: '게임 대회',
            },
            {
                title: '여행 준비 모임',
                start: '2024-10-18',
                description: '여행을 위한 준비 모임',
            }
        ],
        eventClick: function(info) {
            alert('일정: ' + info.event.title + '\n설명: ' + info.event.extendedProps.description);
        }
    });
    
    calendar.render();  // 캘린더 렌더링
});
