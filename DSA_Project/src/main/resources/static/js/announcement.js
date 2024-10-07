$(function() {
	
	// 공지사항 등록 버튼 클릭 이벤트
    $('#postAnnouncement').on('click', function() {
        const announcementText = $('#announcementText').val();
        if (announcementText) {
            $('#announcementText').val('');
            $('#editAnnouncement').removeClass('hidden');
        }
    });

    // 공지사항 수정 버튼 클릭 이벤트
    $('#editAnnouncement').on('click', function() {
        const announcementText = $('#announcementText').val();
        if (announcementText) {
            $('#editAnnouncement').addClass('hidden');
        }
    });

    // 댓글 달기 버튼 클릭 이벤트
    $('#postComment').on('click', function() {
        const commentText = $('#commentInput').val();
        if (commentText) {
            $('#commentsList').append(`
                <div class="comment">
                    <span>${commentText}</span>
                    <button class="editComment">수정</button>
                    <button class="deleteComment">삭제</button>
                </div>
            `);
            $('#commentInput').val('');
        }
    });

    // 댓글 삭제 기능
    $('#commentsList').on('click', '.deleteComment', function() {
        $(this).parent('.comment').remove();
    });

});