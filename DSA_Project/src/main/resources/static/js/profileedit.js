$(document).ready(function(){
    var profileData = {};

    // 프로필 사진 변경 이벤트
    $('#profileImage').change(function() {
        var file = $(this).prop('files')[0];
        if (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('.label-image').css('background-image', 'url(' + e.target.result + ')');
            }
            reader.readAsDataURL(file);
        } else {
            $('.label-image').css('background-image', 'url(../images/default.png)');
        }
    });

    // 관심사 선택 제한 (최대 5개)
    $('input[name="interests"]').change(function() {
        var selectedInterests = $('input[name="interests"]:checked').length;
        if (selectedInterests > 5) {
            alert("관심사는 최대 5개까지 선택 가능합니다.");
            $(this).prop('checked', false);
        }
    });

    // 저장 버튼 클릭 이벤트
    $('#saveProfile').click(function() {
        var nickname = $('#nickname').val();
        var email = $('#email').val();
        var phone = $('#phone').val();
        var selectedInterests = [];

        $('input[name="interests"]:checked').each(function(){
            selectedInterests.push($(this).val());
        });

        if (!nickname || !email || !phone) {
            alert("모든 정보를 입력해 주세요.");
            return;
        }

        profileData.nickname = nickname;
        profileData.email = email;
        profileData.phone = phone;
        profileData.interests = selectedInterests;

        alert(JSON.stringify(profileData)); // 디버깅을 위한 알림
        $.ajax({
            type: 'POST',
            url: '/kkirikkiri/member/updateProfile', // 수정된 URL
            data: JSON.stringify(profileData),
            contentType: 'application/json',
            success: function(response) {
                alert('프로필이 성공적으로 수정되었습니다.');
                window.location.href = "/kkirikkiri/mypage";
            },
            error: function(error) {
                alert("프로필 수정에 실패했습니다.");
            }
        });
    });
});
s