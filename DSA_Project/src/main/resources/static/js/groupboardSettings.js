$(function() {
    var memberLimit = parseInt($('#memberLimit').val());
    var hashtags = []; // 추가된 해시태그 배열
    var removedHashtags = []; // 삭제된 해시태그 배열
    var uploadedImageFile = null;  // 모달에서 선택한 이미지 파일 저장용 변수
    
    $(document).on('click', '.interest-label', function() {
	    $('.interest-label').removeClass('selected'); // 기존 선택 제거
	    $(this).addClass('selected');
	    var radioId = $(this).attr('for'); // 클릭된 라벨의 'for' 속성 값으로 라디오 버튼 ID 가져오기
	    $('#' + radioId).prop('checked', true); // 해당 라디오 버튼 선택
	});

    // 인원 수 감소
    $('#decreaseCount').on('click', function() {
        if (memberLimit > 2) {
            memberLimit--;
            $('#memberLimit').val(memberLimit);  // input 필드의 값을 변경
        } else {
            alert('그룹 인원은 최소 2명 이상이어야 합니다.');
        }
    });

    // 인원 수 증가
    $('#increaseCount').on('click', function() {
        memberLimit++;
        $('#memberLimit').val(memberLimit);  // input 필드의 값을 변경
    });

    // 해시태그 추가 버튼 클릭 이벤트
	$('#addHashtag').on('click', function() {
	    var hashtagText = $('#hashtag').val().trim().replace(/^#/, ''); // 입력된 해시태그 앞의 # 기호 제거
	    
	    if (hashtagText !== '' && !hashtags.includes('#' + hashtagText)) { // 빈 값 및 중복 체크
	        var formattedHashtag = '#' + hashtagText; // UI에서는 # 기호를 포함하여 보여줌
			hashtags.push(hashtagText); // DB에 저장할 때는 # 기호를 제거한 값을 사용
	
			// 해시태그 UI에 추가 (삭제 아이콘과 함께)
	        $('#hashtagContainer').append(
	            '<div class="hashtag-item-container">' +
	                '<span class="hashtag-item">#' + hashtagText + '</span>' +
	                '<button type="button" class="remove-hashtag">' +
	                    '<img src="../images/delete.png" alt="삭제" class="delete-icon">' +
	                '</button>' +
	            '</div>'
	        );
	        /*// UI에 추가
	        $('#hashtagContainer').append(
	            '<span class="hashtag-item">' +
	            formattedHashtag +
	            ' <button type="button" class="remove-hashtag">삭제</button>' +
	            '</span>'
	        );*/
	
	        $('#hashtag').val(''); // 입력창 비우기
	    } else {
	        alert('유효한 해시태그를 입력하세요.');
	    }
	});

    // 동적으로 생성된 해시태그 삭제 버튼에 대한 이벤트 바인딩
	$(document).on('click', '.remove-hashtag', function() {
	    var hashtagText = $(this).parent().text().replace(' 삭제', '').trim();

	    if (hashtagText !== '') {
	        hashtags = hashtags.filter(function(tag) {
	            return tag !== hashtagText;
	        });
	
	        removedHashtags.push(hashtagText); // 삭제된 해시태그 배열에 추가
        	$(this).parent().remove(); // UI에서 해시태그 삭제
	    }
	});

    // 헤더 이미지 관리 버튼 클릭 시 모달 열기
    $('#openHeaderImageModal').on('click', function() {
        $('#headerImageModal').show();  // 모달 보이기
    });

    // 모달 닫기 버튼
    $('.close, #modalCancelUpload').on('click', function() {
        $('#headerImageModal').hide();  // 모달 숨기기
    });

    // 이미지 선택 시 미리보기
    $('#modalHeaderImageInput').on('change', function() {
        var file = this.files[0];
        if (file) {
            uploadedImageFile = file;  // 선택한 이미지 파일 저장
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#modalImagePreview').attr('src', e.target.result).show();  // 이미지 미리보기 보이기
            };
            reader.readAsDataURL(file);
        } else {
            $('#modalImagePreview').hide();  // 이미지 미리보기 숨기기
            uploadedImageFile = null;  // 파일이 없을 때 초기화
        }
    });

    $('#modalUploadHeaderImage').on('click', function() {
	    var file = $('#modalHeaderImageInput')[0].files[0];
	    var groupId = $('#groupId').val();  // groupId 가져오기
	    if (file) {
	        // 이미지 파일 전송
	        var formData = new FormData();
	        formData.append('profileImage', file); // 업로드할 파일 추가
	        formData.append('groupId', groupId);  // groupId 추가
	
	        $.ajax({
	            url: '/kkirikkiri/groupboard/settings', // 기존 URL 사용
	            type: 'POST',
	            data: formData,
	            contentType: false,
	            processData: false,
	            success: function(response) {
	                // 서버 응답으로 이미지 경로 받아오기
	                var uploadedImageUrl = response.imageUrl;  // 서버에서 반환된 이미지 URL
	                
	                // 헤더 이미지 미리보기 업데이트
	                $('#headerImagePreview').attr('src', uploadedImageUrl + '?' + new Date().getTime()); // 캐시 방지용 쿼리 스트링 추가
	                $('#removeImage').show(); // 삭제 버튼 보이기
	                $('#headerImageModal').hide(); // 모달 닫기
	            },
	            error: function(xhr, status, error) {
	                alert('이미지 업로드 중 오류가 발생했습니다. 다시 시도해주세요.');
	            }
	        });
	    } else {
	        alert('업로드할 이미지를 선택하세요.');
	    }
	});

    // 이미지 제거 버튼 클릭 이벤트
    $('#removeImage').on('click', function() {
        $('#imagePreview').attr('src', '../images/noImage_icon.png').show(); // 기본 이미지로 변경
        $('#modalHeaderImageInput').val(''); // 파일 선택 초기화
        $('#modalImagePreview').attr('src', '').hide(); // 모달 미리보기 숨기기
        uploadedImageFile = null; // 선택한 이미지 초기화
        $(this).hide(); // 삭제 버튼 숨기기
    });
    
    var removedMembers = []; // 삭제된 멤버 ID를 저장할 배열
	// 멤버 삭제 버튼 클릭 이벤트
	$(document).on('click', '.remove-member', function() {
	    var userId = $(this).data('user-id');
	    removedMembers.push(userId); // 삭제된 멤버 ID를 배열에 추가
	    $(this).parent().remove(); // UI에서 해당 멤버 제거
	});

    // 그룹 업데이트 버튼 클릭 이벤트
    $('#updateGroupBtn').on('click', function(e) {
        e.preventDefault();  // 폼의 기본 제출 방식 막기

        // 폼 데이터를 가져옴
        var formData = new FormData($('#settingsForm')[0]);

		// 추가된 해시태그에서 빈 값을 제거하고 서버로 전송
	    var validHashtags = hashtags.filter(function(tag) {
	        return tag.trim() !== '';  // 빈 해시태그 필터링
	    });
	    
        // 추가된 해시태그와 삭제된 해시태그 데이터를 추가
        formData.append('hashtags', hashtags.join(',')); // 추가된 해시태그
        formData.append('removedHashtags', removedHashtags.join(',')); // 삭제된 해시태그

        // 헤더 이미지 파일이 선택되었으면 폼 데이터에 추가
        if (uploadedImageFile) {
            formData.append('profileImage', uploadedImageFile);
        }
        
        formData.append('removedMembers', removedMembers.join(',')); // 삭제된 멤버 ID 추가

        // AJAX로 폼 데이터 전송
        $.ajax({
            url: '/kkirikkiri/groupboard/settings/update',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            success: function(response) {
                alert('그룹 설정이 성공적으로 업데이트되었습니다.');
                window.location.reload();  // 페이지 새로고침
            },
            error: function(xhr, status, error) {
                alert('그룹 설정 업데이트 중 오류가 발생했습니다. 다시 시도해주세요.');
            }
        });
    });
});