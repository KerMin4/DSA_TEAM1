$(function() {

	// FormData 객체를 사용하여 파일과 데이터를 함께 전송
	var formData = new FormData();
	var memberLimit = 2;
	var fileUploaded = false; // 파일이 업로드되었는지 여부를 확인하는 변수
	
	// 관심사 선택 이벤트
	$('input[name="interest"]').on('click', function() {
	    $('input[name="interest"]').removeClass('active-button');
	    $(this).addClass('active-button');
	    formData.set('interest', $(this).val());
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
	        fileUploaded = true; // 파일이 업로드되었음을 표시
	        
	        // 파일 업로드 시 삭제 버튼을 보여줌
	        $('#removeFile').show();
	    }
	});
	
	// 파일 삭제 기능 추가
	$('#removeFile').on('click', function() {
	    if (fileUploaded) {
	        // 미리보기 이미지를 기본 이미지로 되돌림
	        $('#imagePreview').attr('src', '/path/to/placeholder.png');
	        // FormData에서 파일 제거
	        formData.delete('profileImage');
	        fileUploaded = false;
	        // 파일 입력 필드 초기화
	        $('#fileInput').val('');
	        
	        // 파일 삭제 후 삭제 버튼 숨김
	        $('#removeFile').hide();
	    } else {
	        alert('업로드된 파일이 없습니다.');
	    }
	});
	
	// 페이지 로드시 파일 삭제 버튼을 숨김 (초기 상태)
	$('#removeFile').hide();
	
	// 가입 권한 선택 이벤트
	$('input[name="joinMethod"]').on('click', function() {
	    $('input[name="joinMethod"]').removeClass('active-button');
	    $(this).addClass('active-button');
	    formData.set('joinMethod', $(this).val());
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
	
	// 툴팁을 위한 마우스 이벤트 처리
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
	
	// 폼 제출 시 데이터 검증 및 제출 처리
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
	
	    // 검증
	    if (!formData.get('interest')) return alert('관심사를 선택해주세요.');
	    if (!formData.get('groupName')) return alert('그룹명을 입력해주세요.');
	    if (!formData.get('eventDate')) return alert('활동 날짜를 선택해주세요.');
	    if (!formData.get('eventTime')) return alert('활동 시간을 선택해주세요.');
	    if (memberLimit < 2) return alert('인원 수는 최소 2명이어야 합니다.');
	    if (!formData.get('description')) return alert('그룹 소개를 작성해주세요.');
	    if (!formData.get('location')) return alert('위치를 선택해주세요.');
	    if (!formData.get('joinMethod')) return alert('가입 권한을 선택해주세요.');
	
	    // 서버로 전송
	    $.ajax({
	        url: $('#createForm').attr('action'),
	        type: 'POST',
	        data: formData,
	        processData: false,
	        contentType: false,
	        success: function(response) {
	            console.log(response);
	            alert('그룹이 성공적으로 생성되었습니다.');
	        },
	        error: function(err) {
	            console.log(err);
	            alert('그룹 생성에 실패했습니다. 다시 시도해주세요.');
	        }
	    });
	
	});

});
