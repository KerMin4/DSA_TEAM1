$(document).ready(function () {

   
  
    $('input[name="interests"]').change(function() {
        const checkedInterests = $('input[name="interests"]:checked').length;

     
        if (!$(this).is(':checked')) {
            console.log('관심사 선택 해제됨:', $(this).val());
        }

      
        if (checkedInterests > 5) {
            alert('취향은 다섯 개까지 선택 가능해요.');
            $(this).prop('checked', false); 
        }
    });

  
    function validateNicknameForm() {
        let isValid = true;
        const nickname = $('#nickname').val();
        const nicknameRegex = /^[가-힣a-zA-Z0-9]{3,5}$/;

        console.log('닉네임 입력 값:', nickname); 

        if (!nicknameRegex.test(nickname)) {
            $('#nicknameError').text('닉네임은 3-5글자 이내로, 특수문자와 공백은 사용할 수 없습니다.').css('color', 'red');
            isValid = false;
        } else {
            $('#nicknameError').text('');
        }

        return isValid;
    }

   
    function validatePasswordForm() {
        let isValid = true;
        const password = $('#password').val();
        const password2 = $('#password2').val();
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;

        console.log('비밀번호 입력 값:', password); 

        if (!passwordRegex.test(password)) {
            $('#passwordError').text('비밀번호는 8자 이상 대문자와 소문자가 포함되어야 하며 공백은 불가합니다.').css('color', 'red');
            isValid = false;
        } else {
            $('#passwordError').text('');
        }

        if (password !== password2) {
            $('#passwordMatchError').text('비밀번호가 일치하지 않습니다.').css('color', 'red');
            isValid = false;
        } else {
            $('#passwordMatchError').text('');
        }

        return isValid;
    }

    
    function validatePhoneForm() {
        let isValid = true;
        const phone = $('#phone').val();
        const phoneRegex = /^\d{10,11}$/;

        console.log('전화번호 입력 값:', phone);

        if (!phoneRegex.test(phone)) {
            $('#phoneError').text('전화번호는 10-11자리 숫자만 입력 가능합니다.').css('color', 'red');
            isValid = false;
        } else {
            $('#phoneError').text('');
        }

        return isValid;
    }

   
    function validateProfileImageForm() {
        let isValid = true;
        const file = $('#profileImageInput').prop('files')[0];

        console.log('프로필 이미지 파일 선택됨:', file);

        if (!file) {
            $('#profileImageError').text('프로필 이미지를 선택해주세요.').css('color', 'red');
            isValid = false;
        } else {
            $('#profileImageError').text('');
        }

        return isValid;
    }

  
    function validateInterestsForm() {
        const checkedInterests = $('input[name="interests"]:checked').length;
        
        if (checkedInterests === 0) {
            alert('관심사를 하나 이상 선택해주세요.');
            return false;
        }

        return true;
    }

    
    function resetForms() {
        $('#nickname').val('');
        $('#password').val('');
        $('#password2').val('');
        $('#phone').val('');
        $('#profileImageInput').val('');  
        $('#profilePreview').attr('src', '').hide();

        $('#nicknameError').text('');
        $('#passwordError').text('');
        $('#passwordMatchError').text('');
        $('#phoneError').text('');
        $('#profileImageError').text('');

        console.log('폼 초기화됨');
    }

  
    function openModal(modalId) {
        console.log(modalId + ' 열림');
        $(modalId).addClass('show'); 
    }

   
    function closeModal(modalId) {
        console.log(modalId + ' 닫힘');
        $(modalId).removeClass('show'); 
        resetForms(); 
    }

   
    $('.close').click(function () {
        closeModal($(this).closest('.modal'));
        console.log('모달 닫기 버튼 클릭됨');
    });

   
    $(window).click(function (event) {
        if ($(event.target).hasClass('modal')) {
            closeModal($(event.target));
            console.log('모달 외부 클릭됨');
        }
    });

  
    $('#editProfileIcon').click(function () {
        console.log('펜 아이콘이 정상적으로 렌더링됨');
        openModal('#editProfileModal');
    });

   
    $('#cameraIcon').click(function () {
        resetForms();
        openModal('#profileImageModal');
        console.log('카메라 아이콘 클릭됨, 프로필 이미지 모달 열림');
    });

  
    $('#nicknameChangeForm .save-button').click(function (event) {
        event.preventDefault();
        if (validateNicknameForm()) {
            console.log('닉네임 변경 폼 제출됨');
            $('#nicknameChangeForm').submit();
        }
    });

  
    $('#passwordChangeForm .save-button').click(function (event) {
        event.preventDefault();
        if (validatePasswordForm()) {
            console.log('비밀번호 변경 폼 제출됨');
            $('#passwordChangeForm').submit();
        }
    });

   
    $('#phoneChangeForm .save-button').click(function (event) {
        event.preventDefault();
        if (validatePhoneForm()) {
            console.log('전화번호 변경 폼 제출됨');
            $('#phoneChangeForm').submit();
        }
    });

   
    $('#fileSelectButton').click(function () {
        console.log('파일 선택 버튼 클릭됨');
        $('#profileImageInput').click();
    });

   
    $('#profileImageInput').change(function () {
        const file = $(this).prop('files')[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                $('#profilePreview').attr('src', e.target.result).show();
                console.log('프로필 이미지 미리보기 표시됨');
            };
            reader.readAsDataURL(file);
        }
    });

   
    $('#profileImageForm .save-button').click(function (event) {
        event.preventDefault();
        if (validateProfileImageForm()) {
            console.log('프로필 이미지 변경 폼 제출됨');
            $('#profileImageForm').submit();
        }
    });

   
    $('#findPlace').click(function () {
        console.log('지역 찾기 버튼 클릭됨');
        window.open('/kkirikkiri/member/mapTest', '지역찾기', 'fullscreen');
    });


    $('#saveLocation').click(function () {
        const location = $('#location').val();
        console.log('입력된 위치 값:', location);
        if (location) {
            console.log('위치 저장 폼 제출됨');
            $('#locationForm').submit();
        } else {
            alert("위치를 입력해주세요.");
        }
    });

    
    $('#interestsForm .save-button').click(function (event) {
        event.preventDefault();
        if (validateInterestsForm()) {
            console.log('관심사 저장 폼 제출됨');
            $('#interestsForm').submit();
        }
    });
});
