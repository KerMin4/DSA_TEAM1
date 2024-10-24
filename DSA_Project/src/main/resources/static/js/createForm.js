$(function() {
    var formData = new FormData(); // FormData 객체 초기화
    var memberLimit = 2;

    // Interest radio button change event
    $('input[type="radio"][name="interest"]').on('change', function() {
        var selectedInterest = $('input[type="radio"][name="interest"]:checked').val();
        formData.set('interest', selectedInterest);  // 선택한 관심사를 FormData에 저장
    });

    // Decrease member count
    $('#decreaseCount').on('click', function() {
        if (memberLimit > 2) {
            memberLimit--;
            $('#memberLimit').text(memberLimit);
            formData.set('memberLimit', memberLimit); // 선택한 멤버 제한을 FormData에 저장
        } else {
            alert('그룹 생성은 최소 2명부터 가능합니다.');
        }
    });

    // Increase member count
    $('#increaseCount').on('click', function() {
        memberLimit++;
        $('#memberLimit').text(memberLimit);
        formData.set('memberLimit', memberLimit); // 선택한 멤버 제한을 FormData에 저장
    });

    // Open header image modal
    $('#openHeaderImageModal').on('click', function() {
        $('#headerImageModal').show(); // 모달 열기
    });

    // Close modal
    $('.close').on('click', function() {
        $('#headerImageModal').hide(); // 모달 닫기
    });

    // Close modal when clicking outside
    $(window).on('click', function(event) {
        if ($(event.target).is('#headerImageModal')) {
            $('#headerImageModal').hide(); // 모달 닫기
        }
    });

    // Image file selection preview
    $('#modalHeaderImageInput').on('change', function() {
        var file = this.files[0];
        if (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#modalImagePreview').attr('src', e.target.result).show(); // 모달 이미지 미리보기 설정
            };
            reader.readAsDataURL(file); // 이미지 파일 읽기
        }
    });

    // Upload image button click event
    $('#modalUploadHeaderImage').on('click', function() {
        var file = $('#modalHeaderImageInput')[0].files[0];
        if (file) {
            // 메인 페이지에 이미지 미리보기 설정
            $('#imagePreview').attr('src', URL.createObjectURL(file)).show();
            $('#removeImage').show(); // 삭제 버튼 보이기
            $('#headerImageModal').hide(); // 모달 닫기
            formData.set('profileImage', file); // FormData에 프로필 이미지 저장
        } else {
            // 선택된 파일이 없을 경우 기본 이미지 설정
            $('#imagePreview').attr('src', '../images/noImage_icon.png').show();
            $('#removeImage').hide(); // 삭제 버튼 숨기기
            formData.set('profileImage', 'noImage_icon.png'); // 기본 이미지를 FormData에 저장
        }
    });

    // Remove image button click event
    $('#removeImage').on('click', function() {
        $('#imagePreview').attr('src', '../images/noImage_icon.png').show(); // 메인 페이지 이미지 기본값으로 초기화
        $('#modalHeaderImageInput').val(''); // 모달 인풋 초기화
        $('#modalImagePreview').attr('src', '').hide(); // 모달 미리보기 초기화
        formData.set('profileImage', 'noImage_icon.png'); // 기본 이미지를 FormData에 저장
        $(this).hide(); // 삭제 버튼 숨기기
    });
    
    // Remove image button click event
    $('#removeImage').on('click', function(e) {
        e.preventDefault(); // 폼 제출 방지
        $('#imagePreview').attr('src', '../images/noImage_icon.png').show(); // 이미지 기본값으로 초기화
        $('#modalHeaderImageInput').val(''); // 모달 인풋 초기화
        $('#modalImagePreview').attr('src', '').hide(); // 모달 미리보기 초기화
        formData.set('profileImage', 'noImage_icon.png'); // 기본 이미지를 FormData에 저장
        $(this).hide(); // 삭제 버튼 숨기기
    });
    
    // 지역찾기 버튼
    $('#findPlace').click(function () {
        console.log('지역 찾기 버튼 클릭됨');
        window.open('/kkirikkiri/member/mapTest', '지역찾기', 'fullscreen');
        
        const location = $('#location').val();
        console.log('입력된 위치 값:', location);

        $('#locationForm').submit();
    });

    // Join method radio button change event
    $('input[type="radio"][name="joinMethod"]').on('change', function() {
	    var selectedJoinMethod = $('input[type="radio"][name="joinMethod"]:checked').val().toUpperCase(); // 값을 대문자로 변환
	    formData.set('joinMethod', selectedJoinMethod);  // 대문자로 변환 후 FormData에 저장
	});	

    // Hashtag registration
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
        
        // 동적으로 생성되는 해시태그 요소
	    var newHashtag = $(`
	        <div class="hashtag-wrapper">
	            <span class="hashtag">#${hashtagValue}</span>
	            <button class="remove-hashtag">
	                <img src="../images/delete.png" alt="삭제" class="delete-icon">
	            </button>
	        </div>
	    `);
        /*var newHashtag = $('<span></span>').addClass('hashtag')
            .html('#' + hashtagValue + '</span><button class="remove-hashtag"></button>');*/
        $('#hashtagContainer').append(newHashtag);
        hashtagInput.val('');

        formData.set('hashtags', formData.hashtags.join(','));  // 해시태그를 FormData에 저장
    });

    // Hashtag removal
    $(document).on('click', '.remove-hashtag', function() {
		var hashtagText = $(this).siblings('.hashtag').text().replace('#', '').trim();
        /*var hashtagText = $(this).parent().text().replace(' 삭제', '');*/
        formData.hashtags = formData.hashtags.filter(function(tag) {
            return tag !== hashtagText;
        });

        formData.set('hashtags', formData.hashtags.join(','));  // FormData에서 해시태그 업데이트
        $(this).parent().remove();
    });

    // Form submission handling
    $('#createForm').on('submit', function(e) {
        e.preventDefault();

        // 필수 데이터 설정
        formData.set('groupName', $('#groupName').val());		// 그룹명 저장
        formData.set('description', $('#description').val());	// 설명 저장
        formData.set('location', $('#location').val()); 		// 위치 저장
        if (!formData.get('memberLimit')) {
            formData.set('memberLimit', memberLimit); 			// 기본 멤버 제한 설정
        }

        // 날짜와 시간을 조합하여 LocalDateTime 형식으로 변환
		var eventDate = $('#eventDate').val(); // 'yyyy-MM-ddTHH:mm' 형식으로 가져옴
		if (!eventDate) return alert('활동 날짜와 시간을 선택해주세요.');
		formData.set('eventDate', eventDate);

        // 필수 필드 검증
        if (!formData.get('interest')) return alert('관심사를 선택해주세요.');
        if (!formData.get('groupName')) return alert('그룹명을 입력해주세요.');
        if (!formData.get('description')) return alert('그룹 소개를 작성해주세요.');
        if (!formData.get('location')) return alert('위치를 선택해주세요.');
        if (!formData.get('joinMethod')) return alert('가입 권한을 선택해주세요.');
        
        // formData의 내용을 확인
        for (let pair of formData.entries()) {
            console.log(pair[0] + ': ' + pair[1]); // 모든 formData 값을 출력
        }

        // 서버에 데이터 전송
        $.ajax({
            url: '/kkirikkiri/socialgroup/create',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
				console.log('서버 응답:', response);
                alert('그룹이 성공적으로 생성되었습니다.');
                window.location.href = '/kkirikkiri/socialgroup/socialing';  // 그룹 목록 페이지로 이동
            },
            error: function(xhr) {
                if (xhr.status === 401) {
                    window.location.href = '/kkirikkiri/member/loginForm'; // 401 오류 시 로그인 페이지로 이동
                } else {
                    console.log("Error Status: ", xhr.status);
                    console.log("Error Response: ", xhr.responseText);
                    console.log("Form Data: ", Array.from(formData.entries())); // FormData의 내용을 로그로 확인
                    alert('그룹 생성에 실패했습니다. 다시 시도해주세요.');
                }
            }
        });
    });
});
