$(document).ready(function() {
    // 연필 아이콘 클릭 시 수정 모달 열기
    $('#editProfileIcon').click(function() {
        $('#editProfileModal').show();
    });

    // 카메라 아이콘 클릭 시 이미지 변경 모달 열기
    $('#cameraIcon').click(function() {
        $('#profileImageModal').show();
    });

    // 모달 닫기
    $('.close').click(function() {
        $(this).closest('.modal').hide(); // 해당 모달만 닫기
    });

    // 모달 외부 클릭 시 닫기
    $(window).click(function(event) {
        if ($(event.target).is('.modal')) {
            $(event.target).hide(); // 클릭한 요소가 모달이면 닫기
        }
    });

    // 프로필 이미지 미리보기
    $('#profileImageInput').change(function() {
        var file = $(this).prop('files')[0];
        if (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#profilePreview').attr('src', e.target.result).show(); // 미리보기 이미지 표시
            };
            reader.readAsDataURL(file);
        }
    });

    // 파일 선택 버튼 클릭 시 파일 입력 클릭
    $('#fileSelectButton').click(function() {
        $('#profileImageInput').click(); // 파일 입력 클릭
    });
});
