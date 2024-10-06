$(function() {
    var formData = new FormData();
    var memberLimit = 2;
    
    // 관심사 라디오 버튼 선택 이벤트
	$('input[type="radio"][name="interest"]').on('change', function() {
	    var selectedInterest = $('input[type="radio"][name="interest"]:checked').val();
	    formData.set('interest', selectedInterest);  // FormData객체에 선택한 관심사 저장
	});
	
	// 인원 수 감소
	$('#decreaseCount').on('click', function() {
	    if (memberLimit > 2) {
	        memberLimit--;
	        $('#memberLimit').text(memberLimit);
	        formData.set('memberLimit', memberLimit);	// FormData객체에 선택한 인원수 저장
	    } else {
	        alert('그룹 생성은 최소 2명부터 가능합니다.');
	    }
	});
	
	// 인원 수 증가
	$('#increaseCount').on('click', function() {
	    memberLimit++;
	    $('#memberLimit').text(memberLimit);
	    formData.set('memberLimit', memberLimit);	// FormData객체에 선택한 인원수 저장
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

    // 이미지 파일 선택 시 미리보기
    $('#modalHeaderImageInput').on('change', function() {
        var file = this.files[0];
        if (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#modalImagePreview').attr('src', e.target.result).show(); // 모달 내 미리보기 설정
            };
            reader.readAsDataURL(file); // 이미지 파일 읽기
        }
    });

    // 이미지 업로드 버튼 클릭 시 이미지 서버에 저장
    $('#modalUploadHeaderImage').on('click', function() {
        var file = $('#modalHeaderImageInput')[0].files[0];
        if (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#imagePreview').attr('src', e.target.result).show(); // 메인 화면에 이미지 미리보기 표시
                $('#removeImage').show(); // 이미지가 있으면 삭제 버튼 보이기
                $('#headerImageModal').hide(); // 모달 닫기
            };
            reader.readAsDataURL(file); // 이미지 파일 읽기
            formData.set('profileImage', file); // FormData 객체에 프로필 이미지 파일을 저장
        }
    });

    // 이미지 삭제 버튼 클릭 시 이미지 초기화
    $('#removeImage').on('click', function() {
        $('#imagePreview').attr('src', '').hide(); // 메인 화면의 미리보기 이미지 초기화
        $('#modalHeaderImageInput').val(''); // 모달 내 파일 입력 초기화
        $('#modalImagePreview').attr('src', '').hide(); // 모달 내 미리보기 이미지 초기화
        formData.delete('profileImage'); // FormData에서 이미지 삭제
        $(this).hide(); // 삭제 버튼 숨기기
    });
    
    // 가입 권한 라디오 버튼 선택 이벤트
    $('input[type="radio"][name="joinMethod"]').on('change', function() {
        var selectedJoinMethod = $('input[type="radio"][name="joinMethod"]:checked').val();
        
        formData.set('joinMethod', selectedJoinMethod);  // FormData객체에 가입권한 값 저장
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

    // 폼 제출 처리
    $('#createForm').on('submit', function(e) {
        e.preventDefault();
        
        formData.set('groupName', $('#groupName').val()); // 그룹 이름 저장
        formData.set('eventDate', $('#eventDate').val()); // 활동 날짜 저장
        formData.set('eventTime', $('#eventTime').val()); // 활동 시간 저장
        formData.set('description', $('#description').val()); // 그룹 설명 저장
        formData.set('location', $('#location').val()); // 위치 저장
        if (!formData.get('memberLimit')) {
            formData.set('memberLimit', memberLimit); // 그룹 인원 제한 저장 (기본값 2)
        }
        
        if (!formData.get('interest')) return alert('관심사를 선택해주세요.');
	    if (!formData.get('groupName')) return alert('그룹명을 입력해주세요.');
	    if (!formData.get('eventDate')) return alert('활동 날짜를 선택해주세요.');
	    if (!formData.get('eventTime')) return alert('활동 시간을 선택해주세요.');
	    if (!formData.get('description')) return alert('그룹 소개를 작성해주세요.');
	    if (!formData.get('location')) return alert('위치를 선택해주세요.');
	    if (!formData.get('joinMethod')) return alert('가입 권한을 선택해주세요.');

        // 서버로 전송
        $.ajax({
            url: '/socialgroup/create',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function() {
                alert('그룹이 성공적으로 생성되었습니다.');
            },
            error: function() {
                alert('그룹 생성에 실패했습니다. 다시 시도해주세요.');
            }
        });
    });
    
    /**
     * 서버를 통해 DB로 전송되는 값:
     * - groupName: 그룹 이름
     * - eventDate: 활동 날짜
     * - eventTime: 활동 시간
     * - description: 그룹 설명
     * - location: 위치
     * - memberLimit: 그룹 인원 제한
     * - interest: 관심사
     * - joinMethod: 가입 권한 (자동 승인/승인 후 가입)
     * - hashtags: 해시태그 목록
     * - profileImage: 그룹 프로필 이미지
     *
     * 위 값들은 formData 객체에 저장되어 서버로 전송되며, 서버에서 해당 데이터를 DB에 저장합니다.
     */

});
