$(function() {
    // URL에서 groupId 값을 가져오는 함수
    function getParameterByName(name) {
        let url = window.location.href;
        name = name.replace(/[\[\]]/g, '\\$&');
        let regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)');
        let results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, ' '));
    }

    // URL에서 groupId 추출
    var groupId = getParameterByName('groupId'); // ★ 여기서 groupId를 가져옴 ★
    console.log("groupId:", groupId);

    // 공지사항 등록 버튼 클릭 이벤트
    $('#postAnnouncement').on('click', function() {
        const announcementText = $('#announcementText').val();
        if (announcementText && groupId) {
            $.ajax({
                type: "POST",
                url: `/kkirikkiri/socialgroup/announcement/post`,
                data: {
                    content: announcementText,
                    groupId: groupId // ★ 동적으로 groupId 사용 ★
                },
                success: function(response) {
                    alert(response);
                    $('#announcementText').val('');  // 입력 필드 초기화
                    $('#editAnnouncement').removeClass('hidden');  // 수정 섹션 표시
                    loadAnnouncements();  // 공지사항 목록 다시 로드
                },
                error: function(error) {
                    alert(error.responseText);
                }
            });
        }
    });

    // 공지사항 목록 로드 함수
    function loadAnnouncements() {
        $.ajax({
            type: "GET",
            url: `/kkirikkiri/socialgroup/announcement`,
            data: {
                groupId: groupId // ★ 동적으로 groupId 사용 ★
            },
	            success: function(announcementList) {
	            console.log("Announcement List: ", announcementList); // 응답 데이터 확인
	            if (Array.isArray(announcementList)) {  // 응답이 배열인지 확인
	                $('#announcementList').empty();
	                announcementList.forEach(function(announcement) {
	                    $('#announcementList').append(`
	                        <div class="announcement">
	                            <p>${announcement.content}</p>
	                            <button class="deleteAnnouncement" data-id="${announcement.postId}">삭제</button>
	                        </div>
	                    `);
	                });
	            } else {
	                alert("공지사항 목록을 불러오는데 문제가 발생했습니다."); // 배열이 아닌 경우 처리
	            }
            
           	},
        	error: function(error) {
            	alert(error.responseText);
        	}
        });
    }

    // 공지사항 삭제 버튼 클릭 이벤트
    $('#announcementList').on('click', '.deleteAnnouncement', function() {
        const postId = $(this).data('id');
        $.ajax({
            type: "POST",
            url: `/kkirikkiri/socialgroup/announcement/delete`,
            data: {
                postId: postId
            },
            success: function(response) {
                alert(response);
                loadAnnouncements();  // 공지사항 목록 다시 로드
            },
            error: function(error) {
                alert(error.responseText);
            }
        });
    });

    // 페이지 로드 시 공지사항 목록 불러오기
    loadAnnouncements();
});
