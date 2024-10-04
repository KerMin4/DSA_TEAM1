$(function() {
    // Tab click event handler
    $('.tabs a').on('click', function(event) {
        event.preventDefault();
        
        // Get the content ID from the data attribute
        const contentToShow = $(this).data('content');
        
        // Hide all content sections
        $('.content-section').addClass('hidden');
        
        // Show the selected content section
        $('#' + contentToShow).removeClass('hidden');
    });

    // 공지사항 등록 버튼 클릭 이벤트
    $('#postAnnouncement').on('click', function() {
        const announcementText = $('#announcementText').val();
        if (announcementText) {
            $('#announcementText').val(''); // 입력 필드 초기화
            $('#editAnnouncement').removeClass('hidden'); // 수정 버튼 표시
        }
    });

    // 공지사항 수정 버튼 클릭 이벤트
    $('#editAnnouncement').on('click', function() {
        const announcementText = $('#announcementText').val();
        if (announcementText) {
            // 수정 로직은 나중에 추가
            $('#editAnnouncement').addClass('hidden'); // 수정 버튼 숨김
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
            $('#commentInput').val(''); // 입력 필드 초기화
        }
    });

    // 댓글 삭제 기능
    $('#commentsList').on('click', '.deleteComment', function() {
        $(this).parent('.comment').remove(); // 해당 댓글 삭제
    });

    // 사진 업로드 버튼 클릭 이벤트
    $('#uploadPhotos').on('click', function() {
        const files = $('#photoUpload')[0].files;
        if (files.length > 0) {
            for (let i = 0; i < files.length; i++) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    $('#uploadedPhotos').append(`
                        <div class="uploaded-photo">
                            <img src="${e.target.result}" style="width:100px; height:100px; margin:5px;">
                            <button class="deletePhoto">삭제</button>
                        </div>
                    `);
                }
                reader.readAsDataURL(files[i]);
            }
            $('#photoUpload').val(''); // 파일 입력 필드 초기화
        }
    });

    // 사진 삭제 기능
    $('#uploadedPhotos').on('click', '.deletePhoto', function() {
        $(this).parent('.uploaded-photo').remove(); // 해당 사진 삭제
    });

    // 그룹 헤더 이미지 업로드 및 미리보기 기능
    $('#uploadHeaderImage').on('click', function() {
        const file = $('#headerImageInput')[0].files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $('#headerImagePreview').attr('src', e.target.result).show(); // 미리보기 이미지 표시
            }
            reader.readAsDataURL(file);
        }
    });
});
