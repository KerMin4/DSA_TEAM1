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
            url: '/kkirikkiri/socialgroup/uploadPost', // 서버 업로드 경로 수정
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
            url: '/kkirikkiri/socialgroup/album/photos',  // groupId를 URL 쿼리로 직접 전달
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

    // 사진 클릭 시 상세 정보 모달 열기
    $('#uploadedPhotos').on('click', '.album-photo', function() {
        var photoId = $(this).data('photo-id');
        var imageUrl = $(this).find('img').attr('src');
        
        // 모달에 이미지 URL 설정
    	$('#photoDetailImage').attr('src', imageUrl);

        $.ajax({
            url: `/kkirikkiri/socialgroup/postDetail/${photoId}`,
            type: 'GET',
            success: function(data) {
                $('#photoUploader').text(data.userId);
                $('#uploadDate').text(data.createdAt);
                $('#photoContent').text(data.content);
                $('#photoDetailModal').show();
            },
            error: function(xhr, status, error) {
                console.error("사진 상세 정보 로드 중 오류 발생:", error);
            }
        });
    });

    // 페이지 로드 시 앨범 사진을 불러옴
    loadPhotos(groupId);
    
    // 상세 모달의 내용을 초기화
    function resetPhotoDetailModal() {
	    $('#photoDetailImage').attr('src', '');  // 이미지를 초기화
	    $('#photoUploader').text('');  // 작성자 이름을 초기화
	    $('#uploadDate').text('');  // 업로드 날짜를 초기화
	    $('#photoContent').text('');  // 내용을 초기화
	}

});
