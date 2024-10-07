$(function() {
	
	var formData = new FormData();
    var memberLimit = 2;

    // 인원 수 감소
    $('#decreaseCount').on('click', function() {
        if (memberLimit > 2) {
            memberLimit--;
            $('#memberLimit').text(memberLimit);
            formData.set('memberLimit', memberLimit);	// FormData 객체에 선택한 인원수 저장
        } else {
            alert('그룹 인원은 최소 2명 이상이어야 합니다.');
        }
    });

    // 인원 수 증가
    $('#increaseCount').on('click', function() {
        memberLimit++;
        $('#memberLimit').text(memberLimit);
        formData.set('memberLimit', memberLimit);	// FormData 객체에 선택한 인원수 저장
    });
    
    // 멤버 추가 로직
	$('#addMemberBtn').on('click', function() {
	    var memberName = $('#addMember').val();
	    if (memberName) {
	        $.ajax({
	            url: '/socialgroup/addMember',
	            type: 'POST',
	            data: { memberName: memberName },
	            success: function(response) {
	                // 멤버 목록을 동적으로 업데이트
	                $('#memberList').append('<li>' + memberName + ' <button class="removeMember" data-member-id="' + response.memberId + '">제거</button></li>');
	            },
	            error: function() {
	                alert('멤버 추가에 실패했습니다.');
	            }
	        });
	    }
	});
	
	// 멤버 제거 로직
	$(document).on('click', '.removeMember', function() {
	    var memberId = $(this).data('member-id');
	    $.ajax({
	        url: '/socialgroup/removeMember',
	        type: 'POST',
	        data: { memberId: memberId },
	        success: function() {
	            $(this).parent().remove(); // UI에서 멤버 삭제
	        },
	        error: function() {
	            alert('멤버 제거에 실패했습니다.');
	        }
	    });
	});

    // 해시태그 등록
    $('#addHashtag').on('click', function() {
        var hashtagInput = $('#hashtag');
        var hashtagValue = hashtagInput.val().trim();
        if (!hashtagValue) return;
        if (formData.hashtags && formData.hashtags.includes(hashtagValue)) {
            alert('이미 등록된 해시태그입니다.');
            hashtagInput.val('');
            return;
        }
        formData.hashtags = formData.hashtags || [];
        formData.hashtags.push(hashtagValue);
        var newHashtag = $('<span></span>').addClass('hashtag')
            .html('#' + hashtagValue + ' <button class="remove-hashtag">삭제</button>');
        $('#hashtagContainer').append(newHashtag);
        hashtagInput.val('');
        formData.set('hashtags', formData.hashtags.join(','));  // FormData 객체에 등록한 해시태그를 저장
    });

    // 해시태그 삭제
    $(document).on('click', '.remove-hashtag', function() {
        var hashtagText = $(this).parent().text().replace(' 삭제', '');
        formData.hashtags = formData.hashtags.filter(function(tag) {
            return tag !== hashtagText;
        });
        formData.set('hashtags', formData.hashtags.join(','));  // FormData 객체에 해시태그 삭제 후 값을 저장
        $(this).parent().remove();
    });

    // 모달을 열기 위한 버튼 클릭 이벤트
    $('#openHeaderImageModal').on('click', function() {
        $('#headerImageModal').show(); // 모달을 보이게 함
    });

    // 모달 닫기 버튼 클릭 이벤트
    $('.close').on('click', function() {
        $('#headerImageModal').hide(); // 모달을 숨김
    });

    // 모달 외부 클릭 시 모달 닫기
    $(window).on('click', function(event) {
        if ($(event.target).is('#headerImageModal')) {
            $('#headerImageModal').hide(); // 모달을 숨김
        }
    });

    // 이미지 선택 시 모달 내 미리보기 기능 추가
    $('#modalHeaderImageInput').on('change', function() {
        var file = this.files[0]; // 파일 가져오기
        if (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#modalImagePreview').attr('src', e.target.result).show(); // 모달 내 이미지 미리보기
            };
            reader.readAsDataURL(file); // 이미지 파일 읽기
        }
    });

    // 이미지 업로드 버튼 클릭 이벤트 (모달 안)
    $('#modalUploadHeaderImage').on('click', function() {
        var file = $('#modalHeaderImageInput')[0].files[0];
        if (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#headerImagePreview').attr('src', e.target.result).show(); // 메인 이미지 미리보기
                $('#removeImage').show(); // 이미지 업로드 후 삭제 버튼 보이기
                $('#headerImageModal').hide(); // 업로드 후 모달 닫기
            };
            reader.readAsDataURL(file); // 이미지 파일 읽기
        }
    });

    // 이미지 삭제 버튼 클릭 이벤트
    $('#removeImage').on('click', function() {
        $('#headerImagePreview').attr('src', '').hide(); // 메인 미리보기 이미지 초기화
        $('#modalHeaderImageInput').val(''); // 파일 입력 초기화
        $('#removeImage').hide(); // 삭제 버튼 숨기기

        // 모달 내용 초기화 (이미지 삭제 시에만 모달 초기화)
        $('#modalImagePreview').attr('src', '').hide(); // 모달 내 미리보기 초기화
    });

    // 취소 버튼 클릭 시 모달 닫기
    $('#modalCancelUpload').on('click', function() {
        $('#headerImageModal').hide(); // 모달 닫기
    });

});
