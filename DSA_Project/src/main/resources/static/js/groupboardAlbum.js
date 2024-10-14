$(function() {
    // 포스트 업로드 모달을 열기 위한 버튼 클릭 이벤트
    $('#openPostModal').on('click', function() {
        $('#postModal').show();
    });

    // 포스트 업로드 모달 닫기 버튼 클릭 이벤트
    $('.close').on('click', function() {
        $('#postModal').hide();
        resetModal();
    });

    // 포스트 업로드 모달 외부 클릭 시 모달 닫기
    $(window).on('click', function(event) {
        if ($(event.target).is('#postModal')) {
            $('#postModal').hide();
            resetModal();
        }
    });

    // 사진 상세보기 모달 닫기 버튼 이벤트 추가
    $('#photoDetailModal .close').on('click', function() {
        $('#photoDetailModal').hide();
        resetPhotoDetailModal();
    });

    // 모달 외부 클릭 시 사진 상세보기 모달 닫기
    $(window).on('click', function(event) {
        if ($(event.target).is('#photoDetailModal')) {
            $('#photoDetailModal').hide();
            resetPhotoDetailModal();
        }
    });

    // 포스트 업로드 모달 이미지 파일 선택 시 미리보기
    $('#modalPostImageInput').on('change', function() {
        var file = this.files[0];
        if (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#modalImagePreview').attr('src', e.target.result).show(); // 모달 내 미리보기 설정
            };
            reader.readAsDataURL(file); // 이미지 파일 읽기
        }
    });

    // 포스트 업로드 버튼 클릭 시 포스트 추가
    $('#modalUploadPost').on('click', function() {
        var file = $('#modalPostImageInput')[0].files[0];
        var groupId = getParameterByName('groupId');
        var description = $('#modalPostTextInput').val();
        
        console.log('groupId:', groupId);
        
        if (!file) {
            alert('사진 파일을 선택하세요.');
            return;
        }
        
        if (!groupId) {
            alert('groupId가 없습니다.');
            return;
        }
    
        var formData = new FormData();
        formData.append('photo', file);
        formData.append('description', description);
        formData.append('groupId', groupId);

        $.ajax({
            url: '/kkirikkiri/groupboard/album/uploadPost', // 서버 업로드 경로 수정
            type: 'POST',
            data: formData,
            processData: false, // 파일 전송을 위해 필요
            contentType: false, // 파일 전송을 위해 필요
            success: function(response) {
                alert('포스트 업로드 성공');
                
                // 서버에서 받은 postId를 사용하여 사진 목록 갱신
                var postId = response.postId;
                console.log('postId:', postId);

                loadPhotos(groupId); // 사진 목록 로드
                
                // 업로드 후 모달 닫기
                $('#postModal').hide();
                resetModal(); // 모달이 닫힐 때 초기화
            },
            error: function(xhr, status, error) {
                console.error("포스트 업로드 중 오류 발생:", error);
                alert('포스트 업로드에 실패했습니다.');
            }
        }); 
    });

    // 포스트 업로드 모달 취소 버튼 클릭 시 모달 닫기
    $('#modalCancelUpload').on('click', function() {
        $('#postModal').hide();
        resetModal();
    });
    
    // 모달 초기화 및 닫기
    function resetModal() {
        $('#modalImagePreview').hide();
        $('#modalPostImageInput').val('');
        $('#modalPostTextInput').val('');
    }

    // 사진 삭제 기능
    $('#uploadedPhotos').on('click', '.deletePhoto', function() {
        $(this).closest('.uploaded-photo').remove();
    });

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

    // 앨범 사진 로드
    function loadPhotos(groupId) {
        console.log("loadPhotos에서 groupId:", groupId);

        if (!groupId) {
            console.error("groupId가 정의되지 않았습니다.");
            return;  // groupId가 없으면 함수를 중단
        }

        $.ajax({
            url: '/kkirikkiri/groupboard/album/photos',  // groupId를 URL 쿼리로 직접 전달
            type: 'GET',
            data: { groupId: groupId },
            success: function(photos) {
                renderPhotos(photos);  // 렌더링 함수로 분리
            },
            error: function(xhr, status, error) {
                console.error("사진 로드 중 오류 발생:", error);
            }
        });
    }

	// 사진을 앨범에 추가하는 로직
	function renderPhotos(photos) {
	    var albumContainer = $('#uploadedPhotos'); // 앨범 컨테이너 선택
	    albumContainer.empty(); // 앨범 초기화
	
	    photos.forEach(function(photo) {
	        var imageUrl = photo.imageName;
	        
	        // 이미지 경로에 '/kkirikkiri/upload/'가 이미 포함되어 있는지 확인
	        if (!imageUrl.startsWith("/kkirikkiri/upload/")) {
	            imageUrl = "/kkirikkiri/upload/" + imageUrl;
	        }
	
	        // 포스트에 연결된 사진을 앨범에 표시
	        var photoElement = `<div class="photo-item album-photo" data-photo-id="${photo.postId}">
	                                <img src="${imageUrl}" alt="사진">
	                            </div>`;
	        albumContainer.append(photoElement);
	    });
	}
	
	// 앨범 사진 클릭 시, 사진 세부 사항 및 댓글 로드
	$('#uploadedPhotos').on('click', '.album-photo', function() {
	    var postId = $(this).data('photo-id');  // 사진 아이템에서 postId 가져오기
	    $('#photoDetailModal').data('post-id', postId);  // 모달에 postId 저장

	    loadPostDetails(postId);  // 포스트 상세 정보 로드
	    loadReplies(postId);  // 댓글 정보 로드
	});
	
    function loadPostDetails(postId) {
	    $.ajax({
	        url: `/kkirikkiri/groupboard/album/postDetail/${postId}`,
	        type: 'GET',
	        success: function(data) {
	            $('#photoUploader').text(data.userId);   // 작성자 정보
	            $('#uploadDate').text(data.createdAt);   // 업로드 날짜
	            $('#photoContent').text(data.content);   // 포스트 내용
	            
            	if (data.photos.length > 0) {
                var imageUrl = data.photos[0].imageName;  // 첫 번째 사진의 경로 사용
                
                // 사진 경로에 '/kkirikkiri/upload/'가 포함되지 않았을 경우 추가
                if (!imageUrl.startsWith("/kkirikkiri/upload/")) {
                    imageUrl = "/kkirikkiri/upload/" + imageUrl;
                }
                
                $('#photoDetailImage').attr('src', imageUrl);  // 모달에 이미지 설정
            } else {
                $('#photoDetailImage').attr('src', '/kkirikkiri/upload/default.jpg');  // 기본 이미지
            }

	            
	            $('#photoDetailModal').show(); // 모달 표시
	        },
	        error: function(xhr, status, error) {
	            console.error("사진 상세 정보 로드 중 오류 발생:", error);
	        }
	    });
	}
	
	// 댓글 목록 로드 함수
	function loadReplies(postId) {
	    $.ajax({
	        url: `/kkirikkiri/groupboard/post/${postId}/replies`,
	        type: 'GET',
	        success: function(replies) {
	            renderReplies(replies);   // 댓글 렌더링 함수 호출
	        },
	        error: function(xhr, status, error) {
	            console.error("댓글 로드 중 오류 발생:", error);
	        }
	    });
	}
	
	// 댓글 목록 렌더링 함수
	function renderReplies(replies) {
	    var replyList = $('#replyList');   // 댓글 목록이 표시될 영역
	    replyList.empty();   // 기존 댓글 초기화
	
	    replies.forEach(function(reply) {
	        var replyItem = `
	            <div class="reply-item">
	                <p><strong>${reply.userId}</strong> (${reply.createdAt}):</p>
	                <p>${reply.content}</p>
	                <button class="saveEditReply" data-id="${reply.replyId}" style="display:none;">저장</button>
                    <button class="editReply">수정</button>
                    <button class="deleteReply" data-id="${reply.replyId}">삭제</button>
	            </div>
	        `;
	        replyList.append(replyItem);
	    });
	}

    // 댓글 작성 버튼 클릭 이벤트
    $('#submitReplyButton').on('click', function() {
        var content = $('#replyInput').val();
        var postId = $('#photoDetailModal').data('post-id');  // 모달에서 postId 가져오기
        
    	console.log('Post ID from photoDetailModal:', postId);

        if (!content) {
            alert("댓글 내용을 입력하세요.");
            return;
        }

        $.ajax({
            url: `/kkirikkiri/groupboard/post/${postId}/reply`,
            type: 'POST',
            data: { content: content },
            success: function() {
                alert("댓글 작성 완료");
                $('#replyInput').val(''); // 입력란 초기화
                loadReplies(postId); // 댓글 목록 다시 로드
            },
            error: function(xhr, status, error) {
                console.error("댓글 작성 중 오류 발생:", error);
            }
        });
    });
    
    // 수정 버튼 클릭 이벤트
    $('#replyList').on('click', '.editReply', function () {
		const replyDiv = $(this).closest('.reply-item');
	    const contentElement = replyDiv.find('p').eq(1); // 댓글 내용을 담은 <p> 요소를 가져옴
	    const currentContent = contentElement.text(); // 현재 댓글 내용을 가져옴
	    
	    // <textarea>로 변경하여 수정 가능하게 만듦
	    const textarea = $('<textarea>')
	        .val(currentContent) // 현재 댓글 내용을 넣음
	        .addClass('reply-content') // textarea에 클래스 추가
	        .css({ 'width': '100%', 'resize': 'none' });
	    
	    contentElement.replaceWith(textarea); // <p>를 <textarea>로 대체
	    replyDiv.find('.saveEditReply').show(); // 저장 버튼 표시
	    $(this).hide(); // 수정 버튼 숨김
        /*const replyDiv = $(this).closest('.reply-item');
        const contentElement = replyDiv.find('.reply-content');

        // textarea를 수정 가능하게 하고, 저장 버튼 보이기
        contentElement.prop('disabled', false).focus();  // 수정 가능하게 활성화
        replyDiv.find('.saveEditReply').show();
        $(this).hide(); // 수정 버튼 숨김*/
    });

    // 댓글 수정 저장 버튼 클릭 이벤트
    $('#replyList').on('click', '.saveEditReply', function () {
        const replyId = $(this).data('id');
        const replyDiv = $(this).closest('.reply-item');
        const editedContent = replyDiv.find('.reply-content').val(); // 수정된 텍스트 가져오기

        if (editedContent) {
            $.ajax({
                type: 'POST',
                url: `/kkirikkiri/groupboard/reply/${replyId}/edit`,
                data: { content: editedContent },
                success: function (response) {
                    alert("댓글 수정 완료");
                    
	                // <textarea>를 <p>로 다시 변경하여 수정 불가 상태로 만듦
	                const newContent = $('<p>').text(editedContent);
	                replyDiv.find('textarea').replaceWith(newContent); // <textarea>를 <p>로 변경
	                
	                replyDiv.find('.saveEditReply').hide(); // 저장 버튼 숨김
	                replyDiv.find('.editReply').show(); // 수정 버튼 다시 표시
                    /*replyDiv.find('.reply-content').prop('disabled', true); // 수정 불가 상태로 변경
                    replyDiv.find('.saveEditReply').hide();
                    replyDiv.find('.editReply').show(); // 수정 버튼 다시 보이기*/
                },
                error: function (error) {
                    alert("댓글 수정 중 오류 발생");
                }
            });
        }
    });

    // 댓글 삭제 버튼 클릭 이벤트
    $('#replyList').on('click', '.deleteReply', function () {
        const replyId = $(this).data('id');
        const postId = $('#photoDetailModal').data('post-id');
        if (confirm("정말로 이 댓글을 삭제하시겠습니까?")) {
			
            $.ajax({
                type: 'POST',
                url: `/kkirikkiri/groupboard/reply/${replyId}/delete`,
                success: function (response) {
                    alert("댓글 삭제 완료");
                    loadReplies(postId); // 댓글 목록 다시 로드
                },
                error: function (error) {
                    alert("댓글 삭제 중 오류 발생");
                }
            });
        }
    });
    
    
    // 페이지 로드 시 앨범 사진을 불러옴
    loadPhotos(groupId);
    
    // 상세 모달의 내용을 초기화
    function resetPhotoDetailModal() {
	    $('#photoDetailImage').attr('src', '');
	    $('#photoUploader').text('');
	    $('#uploadDate').text('');
	    $('#photoContent').text('');
	    $('#replyInput').val('');
	    $('#replyList').empty();
	    $('#photoDetailModal').removeData('post-id');
	}

});
