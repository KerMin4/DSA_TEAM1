$(function() {
    var memberLimit = parseInt($('#memberLimit').val());
    var hashtags = []; // 해시태그 배열

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
        var hashtagText = $('#hashtag').val().trim();

        if (hashtagText !== '') {
            // 해시태그 배열에 추가
            hashtags.push(hashtagText);

            // 해시태그 UI에 추가
            $('#hashtagContainer').append(
                '<span class="hashtag-item">' +
                '#' + hashtagText +
                ' <button type="button" class="remove-hashtag">삭제</button>' +
                '</span>'
            );

            // 해시태그 입력창 비우기
            $('#hashtag').val('');
        }
    });

    // 동적으로 생성된 해시태그 삭제 버튼에 대한 이벤트 바인딩
    $(document).on('click', '.remove-hashtag', function() {
        // 클릭한 해시태그 텍스트를 추출
        var hashtagText = $(this).parent().text().replace(' 삭제', '').trim();

        // 해시태그 배열에서 해당 해시태그 제거
        hashtags = hashtags.filter(function(tag) {
            return tag !== hashtagText;
        });

        // 화면에서 해시태그 삭제
        $(this).parent().remove();
    });

    // 그룹 업데이트 버튼 클릭 이벤트 (해시태그도 함께 전송)
    $('#updateGroupBtn').on('click', function(e) {
        e.preventDefault();  // 폼의 기본 제출 방식 막기

        // 폼 데이터를 가져옴
        var formData = new FormData($('#settingsForm')[0]);

        // 해시태그 데이터를 추가
        formData.append('hashtags', hashtags.join(','));

        // AJAX로 폼 데이터 전송
        $.ajax({
            url: '/kkirikkiri/socialgroup/update',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            success: function(response) {
                alert('그룹 설정이 성공적으로 업데이트되었습니다.');
                loadSettingsContent('/kkirikkiri/socialgroup/settings?groupId=' + $('#groupId').val());
            },
            error: function(xhr, status, error) {
                alert('그룹 설정 업데이트 중 오류가 발생했습니다. 다시 시도해주세요.');
            }
        });
    });

    // 설정 관련 함수
    function loadSettingsContent(url) {
        $.ajax({
            url: url,
            method: 'GET',
            success: function(data) {
                // settings.html에서 메인 콘텐츠만 로드하기 위해 .main-content 부분만 업데이트
                var newContent = $(data).find('.main-content').html();
                $('#content-box').html(newContent);

                // 새로운 콘텐츠가 로드된 후에 다시 이벤트를 바인딩
                rebindEventHandlers();
            },
            error: function(xhr, status, error) {
                console.error("AJAX 요청 실패: " + error);
            }
        });
    }

    // 페이지가 처음 로드될 때 이벤트 핸들러를 처음 바인딩하는 부분
    function rebindEventHandlers() {
        // 이벤트 핸들러를 새로 바인딩하는 부분을 추가
    }

    rebindEventHandlers();

    // URL 경로 확인
    var groupId = $('#groupId').val();
    console.log("groupId: " + groupId);
});


/*$(function() {
    var memberLimit = parseInt($('#memberLimit').val());
    
    // 해시태그 리스트를 유지하는 배열
    var hashtags = [];

    // 해시태그 추가 버튼 클릭 이벤트
    $('#addHashtag').on('click', function() {
        var hashtagText = $('#hashtag').val().trim();

        if (hashtagText !== '') {
            // 해시태그 배열에 추가
            hashtags.push(hashtagText);

            // 해시태그 UI에 추가
            $('#hashtagContainer').append(
                '<span class="hashtag-item">' +
                '#' + hashtagText +
                ' <button type="button" class="remove-hashtag">삭제</button>' +
                '</span>'
            );

            // 해시태그 입력창 비우기
            $('#hashtag').val('');
        }
    });

    // 동적으로 생성된 해시태그 삭제 버튼에 대한 이벤트 바인딩
    $(document).on('click', '.remove-hashtag', function() {
        // 클릭한 해시태그 텍스트를 추출
        var hashtagText = $(this).parent().text().replace(' 삭제', '').trim();

        // 해시태그 배열에서 해당 해시태그 제거
        hashtags = hashtags.filter(function(tag) {
            return tag !== hashtagText;
        });

        // 화면에서 해시태그 삭제
        $(this).parent().remove();
    });
    
	// 새로 로드된 폼에 이벤트 핸들러를 다시 설정하는 함수
    function rebindEventHandlers() {
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
	    
		// 그룹 업데이트 버튼 클릭 이벤트 (기존 기능에 AJAX 추가)
	    $('#updateGroupBtn').on('click', function(e) {
	        e.preventDefault();  // 폼의 기본 제출 방식을 막음

	        // 폼 데이터를 가져옴
	        var formData = new FormData($('#settingsForm')[0]);
	
	        // AJAX를 통해 서버로 폼 데이터를 전송
	        $.ajax({
	            url: '/kkirikkiri/socialgroup/update',  // 서버 업데이트 요청 URL
	            type: 'POST',
	            data: formData,
	            contentType: false,
	            processData: false,
	            success: function(response) {
	                // 서버로부터 성공 응답을 받았을 때
	                alert('그룹 설정이 성공적으로 업데이트되었습니다.');
	                // settings 탭의 폼만 다시 로드 (헤더와 사이드바는 유지)
	                loadSettingsContent('/kkirikkiri/socialgroup/settings?groupId=' + $('#groupId').val());
	            },
	            error: function(xhr, status, error) {
	                // 에러가 발생했을 때
	                alert('그룹 설정 업데이트 중 오류가 발생했습니다. 다시 시도해주세요.');
	            }
	         });   
        });
    }   
    
    // 설정 관련 함수
	function loadSettingsContent(url) {
	    $.ajax({
	        url: url,
	        method: 'GET',
	        success: function(data) {
	            // settings.html에서 메인 콘텐츠만 로드하기 위해 .main-content 부분만 업데이트
                var newContent = $(data).find('.main-content').html();  // .main-content 안의 내용만 추출
                $('#content-box').html(newContent);  // 기존 content-box에 새 내용 넣기

                // 새로운 콘텐츠가 로드된 후에 다시 이벤트를 바인딩
                rebindEventHandlers();
	        },
	        error: function(xhr, status, error) {
	            console.error("AJAX 요청 실패: " + error);
	        }
	    });
	}
	
	// 페이지가 처음 로드될 때 이벤트 핸들러를 처음 바인딩하는 부분
    rebindEventHandlers();
    
    // 멤버 추가 로직
    $('#addMemberBtn').on('click', function() {
        var memberName = $('#addMember').val();
        if (memberName) {
            $.ajax({
                url: '/socialgroup/addMember',
                type: 'POST',
                data: { memberName: memberName },
                success: function(response) {
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
        var button = $(this);
        $.ajax({
            url: '/socialgroup/removeMember',
            type: 'POST',
            data: { memberId: memberId },
            success: function() {
                button.parent().remove(); // UI에서 멤버 삭제
            },
            error: function() {
                alert('멤버 제거에 실패했습니다.');
            }
        });
    });

    // 해시태그 삭제
    $(document).on('click', '.remove-hashtag', function() {
        var hashtagText = $(this).parent().text().replace(' 삭제', '');
        $(this).parent().remove();  // 해시태그 UI에서 삭제
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
            $('#headerImagePreview').attr('src', URL.createObjectURL(file)).show();  // 메인 이미지 미리보기
            $('#removeImage').show(); // 이미지 업로드 후 삭제 버튼 보이기
            $('#headerImageModal').hide(); // 업로드 후 모달 닫기
        }
    });

    // 이미지 삭제 버튼 클릭 이벤트
    $('#removeImage').on('click', function() {
        $('#headerImagePreview').attr('src', '').hide(); // 메인 미리보기 이미지 초기화
        $('#modalHeaderImageInput').val(''); // 파일 입력 초기화
        $('#removeImage').hide(); // 삭제 버튼 숨기기
    });

    // 취소 버튼 클릭 시 모달 닫기
    $('#modalCancelUpload').on('click', function() {
        $('#headerImageModal').hide(); // 모달 닫기
    });

    // URL 경로 확인
    var groupId = 1;  // 실제로 동적으로 값을 받아오는지 확인
    console.log("groupId: " + groupId);  // 값이 제대로 출력되는지 확인

    $.ajax({
        url: '/kkirikkiri/socialgroup/settings',
        type: 'GET',
        data: { groupId: groupId },
        success: function(response) {
            console.log("AJAX 요청 성공");
        },
        error: function(xhr, status, error) {
            console.log("AJAX 요청 실패");
            console.error("상태 코드: " + xhr.status);
            console.error("에러 메시지: " + xhr.responseText);
        }
    });
    
});*/