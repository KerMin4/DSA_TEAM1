$(function() {

	var formData = new FormData();
	var memberLimit = 2;
	var fileUploaded = false; 
	
	// 관심사 라디오 버튼 선택 이벤트
	$('input[type="radio"][name="interest"]').on('change', function() {
	    var selectedInterest = $('input[type="radio"][name="interest"]:checked').val();
	    formData.set('interest', selectedInterest);  // 선택한 관심사를 FormData에 저장
	});
	
	// 인원 수 감소
	$('#decreaseCount').on('click', function() {
	    if (memberLimit > 2) {
	        memberLimit--;
	        $('#memberLimit').text(memberLimit);
	        formData.set('memberLimit', memberLimit);
	    } else {
	        alert('그룹 생성은 최소 2명부터 가능합니다.');
	    }
	});
	
	// 인원 수 증가
	$('#increaseCount').on('click', function() {
	    memberLimit++;
	    $('#memberLimit').text(memberLimit);
	    formData.set('memberLimit', memberLimit);
	});
	
	// 파일 첨부 미리보기 및 삭제
	$('input[type="file"]').on('change', function(event) {
	    var file = event.target.files[0];
	    if (file) {
	        formData.set('profileImage', file);
	        var reader = new FileReader();
	        reader.onload = function(e) {
	            $('#imagePreview').attr('src', e.target.result).show();
	        };
	        reader.readAsDataURL(file);
	        fileUploaded = true;
	        
	        $('#removeFile').show();
	    }
	});
	
	// 파일 삭제 기능 추가
	$('#removeFile').on('click', function() {
	    if (fileUploaded) {
	        $('#imagePreview').attr('src', '/path/to/placeholder.png');
	        formData.delete('profileImage');
	        fileUploaded = false;
	        $('#fileInput').val('');
	        
	        $('#removeFile').hide();
	    } else {
	        alert('업로드된 파일이 없습니다.');
	    }
	});
	
	$('#removeFile').hide();
	
	// 가입 권한 라디오 버튼 선택 이벤트
	$('input[type="radio"][name="joinMethod"]').on('change', function() {
	    var selectedJoinMethod = $('input[type="radio"][name="joinMethod"]:checked').val();
	    formData.set('joinMethod', selectedJoinMethod);  // 선택한 가입 권한을 FormData에 저장
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
	    formData.set('hashtags', formData.hashtags.join(','));
	});
	
	// 해시태그 삭제
	$(document).on('click', '.remove-hashtag', function() {
	    var hashtagText = $(this).parent().text().replace(' 삭제', '');
	    formData.hashtags = formData.hashtags.filter(function(tag) {
	        return tag !== hashtagText;
	    });
	    formData.set('hashtags', formData.hashtags.join(','));
	    $(this).parent().remove();
	});
	
	// 툴팁 이벤트 처리
	var tooltip = $('#tooltip');
	$('#fileInput').on('mouseenter', function(event) {
	    if (!fileUploaded) {
	        tooltip.css({
	            display: 'block',
	            top: event.pageY + 10,
	            left: event.pageX + 10
	        });
	    }
	}).on('mousemove', function(event) {
	    if (!fileUploaded) {
	        tooltip.css({
	            top: event.pageY + 10,
	            left: event.pageX + 10
	        });
	    }
	}).on('mouseleave', function() {
	    tooltip.css('display', 'none');
	});
	
	// 폼 제출 처리
	$('#createForm').on('submit', function(e) {
	    e.preventDefault();
	    
	    formData.set('groupName', $('#groupName').val());
	    formData.set('eventDate', $('#eventDate').val());
	    formData.set('eventTime', $('#eventTime').val());
	    formData.set('description', $('#description').val());
	    formData.set('location', $('#location').val());
	    if (!formData.get('memberLimit')) {
	        formData.set('memberLimit', memberLimit);
	    }

	    if (!formData.get('interest')) return alert('관심사를 선택해주세요.');
	    if (!formData.get('groupName')) return alert('그룹명을 입력해주세요.');
	    if (!formData.get('eventDate')) return alert('활동 날짜를 선택해주세요.');
	    if (!formData.get('eventTime')) return alert('활동 시간을 선택해주세요.');
	    if (!formData.get('description')) return alert('그룹 소개를 작성해주세요.');
	    if (!formData.get('location')) return alert('위치를 선택해주세요.');
	    if (!formData.get('joinMethod')) return alert('가입 권한을 선택해주세요.');
	
	    $.ajax({
			url: '/socialgroup/create',
	        type: 'POST',
	        data: formData,
	        processData: false,
	        contentType: false,
	        success: function(response) {
	            alert('그룹이 성공적으로 생성되었습니다.');
	        },
	        error: function(err) {
	            alert('그룹 생성에 실패했습니다. 다시 시도해주세요.');
	        }
	    });
	});

});
