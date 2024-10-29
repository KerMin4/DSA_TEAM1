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
    var groupId = getParameterByName('groupId');
    console.log("groupId:", groupId);

    // 공지사항 등록 버튼 클릭 이벤트
    $('#postAnnouncement').on('click', function() {
        const announcementText = $('#announcementText').val();
        if (announcementText && groupId) {
            $.ajax({
                type: "POST",
                url: `/kkirikkiri/groupboard/announcement/post`,
                data: {
                    content: announcementText,
                    groupId: groupId
                },
                success: function(response) {
                    alert(response);
                    $('#announcementText').val('');  // 입력 필드 초기화
                    location.reload(); // 페이지 새로고침
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
            url: `/kkirikkiri/groupboard/announcement/list`,
            data: {
                groupId: groupId
            },
            success: function(announcementList) {
                console.log("Announcement List: ", announcementList);

                if (Array.isArray(announcementList) && announcementList.length > 0) {
                    $('#announcementList').empty();
                    announcementList.forEach(function(announcement) {
                        
                        // 작성일 포맷 확인
                        let formattedDate = announcement.createdAt ? new Date(announcement.createdAt).toLocaleString() : '작성일 없음';
                        
                        // 수정 및 삭제 버튼 조건: 현재 사용자가 그룹 리더일 경우에만 표시
                        const editButton = (currentUserId === groupLeaderId) ? `<button class="editAnnouncement" data-id="${announcement.postId}">수정</button>` : '';
                        const deleteButton = (currentUserId === groupLeaderId) ? `<button class="deleteAnnouncement" data-id="${announcement.postId}">삭제</button>` : '';
                        
                        // 공지사항 요소 생성
                        $('#announcementList').append(`
                            <div class="announcement" data-id="${announcement.postId}">
                                <div style="display: flex; align-items: center;">
                                    <p class="announcement-content">${announcement.content}</p>
                                    <div class="announcement-meta">
                                        <span class="author">작성자: ${announcement.userId}</span>
                                        <span class="date">작성일: ${formattedDate}</span>
                                    </div>
                                    <button class="saveEdit" data-id="${announcement.postId}" style="display:none;">저장</button>
                                    ${editButton}
                                    ${deleteButton}
                                </div>
                            </div>
                        `);
                    });
                } else {
                    $('#announcementList').html('<p>공지사항이 없습니다.</p>');
                }
            },
            error: function(error) {
                alert("공지사항 목록을 불러오는데 문제가 발생했습니다.");
            }
        });
    }

    // 토글 함수: 수정 버튼 숨기고 저장 버튼 보이기
    function toggleEditButtons(announcementDiv) {
        const saveEditBtn = announcementDiv.find('.saveEdit');
        const editAnnouncementBtn = announcementDiv.find('.editAnnouncement');
        
        // 수정 버튼 숨기고 저장 버튼 보이기
        editAnnouncementBtn.css('display', 'none');
        saveEditBtn.css('display', 'block');
    }
    
    // 저장 버튼 숨기고 수정 버튼 다시 보이기
    function toggleSaveButtons(announcementDiv) {
        const saveEditBtn = announcementDiv.find('.saveEdit');
        const editAnnouncementBtn = announcementDiv.find('.editAnnouncement');
        
        // 저장 버튼 숨기고 수정 버튼 다시 보이기
        saveEditBtn.css('display', 'none');
        editAnnouncementBtn.css('display', 'block');
    }
    
    // 수정 버튼 클릭 이벤트 추가
    $('#announcementList').on('click', '.editAnnouncement', function() {
        const announcementDiv = $(this).closest('.announcement');
        const contentElement = announcementDiv.find('.announcement-content');
        const currentContent = contentElement.text(); // 현재 내용을 가져옴
        
        // p태그를 textarea로 변경하여 수정 가능하게 만듦
        const textarea = $('<textarea>')
            .val(currentContent) // 현재 공지 내용을 넣음
            .addClass('announcement-content') // textarea에 클래스 추가
            .css({ 'width': '100%', 'resize': 'none' });
        
        contentElement.replaceWith(textarea); // <p>를 <textarea>로 대체
        announcementDiv.find('.saveEdit').show(); // 저장 버튼 표시
        $(this).hide(); // 수정 버튼 숨김
    });

    // 저장 버튼 클릭 시 수정 내용을 DB에 반영하는 이벤트 추가
    $('#announcementList').on('click', '.saveEdit', function() {
        const postId = $(this).data('id');
        const announcementDiv = $(this).closest('.announcement');
        const editedContent = announcementDiv.find('.announcement-content').val(); // 수정된 텍스트 가져오기

        if (editedContent) {
            $.ajax({
                type: "POST",
                url: `/kkirikkiri/groupboard/announcement/edit`,
                data: { postId: postId, content: editedContent },
                success: function(response) {
                    alert(response);

                    // textarea를 다시 p태그로 변경하여 수정 불가 상태로 만듦
                    const newContent = $('<p>')
                        .text(editedContent)
                        .addClass('announcement-content');
                    announcementDiv.find('textarea').replaceWith(newContent); // <textarea>를 <p>로 변경
                    
                    announcementDiv.find('.saveEdit').hide(); // 저장 버튼 숨김
                    announcementDiv.find('.editAnnouncement').show(); // 수정 버튼 다시 표시
                },
                error: function(error) {
                    alert(error.responseText);
                }
            });
        }
    });

    // 삭제 버튼 클릭 이벤트 추가 (삭제 후 목록 다시 로드)
    $('#announcementList').on('click', '.deleteAnnouncement', function() {
        const postId = $(this).data('id');
        if (confirm("정말로 이 공지사항을 삭제하시겠습니까?")) {
            $.ajax({
                type: "POST",
                url: `/kkirikkiri/groupboard/announcement/delete`,
                data: { postId: postId },
                success: function(response) {
                    alert(response);
                    loadAnnouncements();  // 공지사항 목록 다시 로드
                },
                error: function(error) {
                    alert(error.responseText);
                }
            });
        }
    });

    // 페이지 로드 시 공지사항 목록 불러오기
    loadAnnouncements();
});
