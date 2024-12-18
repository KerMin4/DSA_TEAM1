$(document).ready(function(){
    var formData = {};
    $('#nextStep1').click(function(){
        var selectedInterests = [];
        $('input[name="interests"]:checked').each(function(){
            selectedInterests.push($(this).val());
        });

        if(selectedInterests.length > 0){
            formData.interests = selectedInterests;
            $('#step1').removeClass('active');
            $('#step2').addClass('active');
        } else {
            alert("최소한 하나의 취향을 선택해 주세요.");
            return false;
        }
        showStep(2);
    });

	$('#findPlace').click(function(){
		window.open('mapTest', '지역찾기', 'fullscreen');	
	});
	
    $('#nextStep2').click(function(e){
        e.preventDefault();
        var location = $('#location').val();
        if(location){
            formData.location = location;
            $('#step2').removeClass('active');
            $('#step3').addClass('active');
        } else{
            alert("관심 지역을 입력해 주세요.");
            return false;
        }
        showStep(3);
    });

    $('#submitForm').click(function(e) {
        var formData = new FormData(); // 이거 추가함. 프로필 사진 업로드하려면 있어야됨 -나연-
        var userid = $('#userid').val();
        var password = $('#password').val();
        var password2 = $('#password2').val();
        var phone = $('#phone').val();
        var email = $('#email').val();
        var username = $('#username').val();
        var name = $('#name').val();
        var profileImage = $('#profileImage')[0].files[0]; // 프로필 사진

        if (!userid || !password || !password2 || !phone || !email || !name || !username) {
            alert("모든 정보를 입력해 주세요.");
            return;
        }

        if (password !== password2) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }
		var year = $('#birth-year').val();
		var month = $('#birth-month').val();
		var day = $('#birth-day').val();
		
		//var birth = 10000*year + 100*month + 1*day;

		if(!year || !month || !day){
			alert("생년월일을 입력해주세요");
			return false;
		}

		var gender = $('input[name="gender"]:checked').val();
		
        formData.append('userid', userid);
        formData.append('password', password);
        formData.append('phone', phone);
        formData.append('email', email);
        formData.append('name', name);
        formData.append('username', username);
        formData.append('profileImage', profileImage); // 프로필 추가
     //   formData.append('birth', birth);
     	formData.append('year', year);
     	formData.append('month', month);
     	formData.append('day', day);
		formData.append('gender', gender);
        console.log(formData);
        /*
        var dataToSend = {
			userid : formData.get('userid'),
			password : formData.get('password'),
			phone : formData.get('phone'),
			email : formData.get('email'),
			name : formData.get('name'),
			username : formData.get('username'),
			profileImage : formData.get('profileImage'),
			year : formData.get('year'),
			month : formData.get('month'),
			day : formData.get('day'),
			gender : formData.get('gender'),
			interests : formData.get('interests'),
			location : formData.get('location'),
		};*/
        $.post("/kkirikkiri/member/join1", formData, function(response) {
            window.location.href = "/kkirikkiri"; 
        }).fail(function(error) {
            console.log(formData); 
        });
        /*
	   $.ajax({
			url: "/kkirikkiri/member/join1",
			type : "POST",
			data: formData,
			contentType : false,
			processData: false,
			success: function(response){
				alert("회원가입이 완료되었습니다.");
				window.location.href = "/kkirikkiri";
			}, error: function(error){
				console.error("오류발생", error);
				alert("회원가입에 실패했습니다.");
			}
	   });
	   
	   */
    });

    function showStep(step){
        $('.step').hide();
        $('#step' + step).show();
        updateStepper(step);
    }

    $('#prevStep1').click(function(e){
        e.preventDefault();
        window.location.href= '/kkirikkiri/';
    });

    $('#prevStep2').click(function(e){
        e.preventDefault();
        showStep(1);
    });
    $('#prevStep3').click(function(e){
        e.preventDefault();
        showStep(2);
    });
    
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

    $('input[name="interests"]').change(function() {
    var selectedInterests = $('input[name="interests"]:checked').length;
    console.log('현재 선택한 취향 개수:', selectedInterests); 
    
    if (selectedInterests > 5) {
        alert("취향은 다섯 개까지 선택 가능해요.");
        $(this).prop('checked', false);
    } else {
      
        $('.interest-text').css('background-color', '#FFFFFF').css('color', '#000000'); 
        $('input[name="interests"]:checked').each(function() {
            $(this).siblings('.interest-text').css('background-color', '#F8B1A3').css('color', '#000000');
        });
    }
});


    function updateStepper(step) {
        $('.step-circle').removeClass('active');
        $('.line').removeClass('active');

        for (var i = 1; i <= step; i++) {
            $('#step-circle-' + i).addClass('active');
            if (i < step) {
                $('#line-' + i).addClass('active');
            }
        }
    }

    $('.step-circle').click(function() {
        var step = $(this).text();
        console.log('스텝퍼 클릭, 이동할 스텝:', step); 

        if (step == 2 && !validateStep1()) return;
        if (step == 3 && !validateStep2()) return;
        showStep(step);
    });      

    $('#duplicateCheck').click(function(){
        let userid = {userid : $('#userid').val()};
        if($('#userid').val().length <3 || $('#userid').val().length > 10){
			alert("아이디는 3~10글자로 해주세요.");
			return;
		}
        if($('#userid').val()){
            $.ajax({
                url: '/kkirikkiri/member/idCheck',
                type : 'post',
                data: userid,
                dataType: 'text', 
                success: function(msg){
                    console.log('실행 성공');
                    if(msg == 'false'){
                        alert("중복된 아이디가 있습니다.");
                        $('#userid').val('');
                    } else{
                        alert("사용 가능한 아이디 입니다!");
                    }
                }, error : function(e){
                    console.log('실행 실패');    
                }
            });
        }
    });    
    
    let isYearOptionExisted = false;
    let isMonthOptionExisted = false;
    let isDayOptionExisted = false;

    $('#birth-year').on('focus', function() {
        if (!isYearOptionExisted) {
            isYearOptionExisted = true;
            for (let i = 2024; i >= 1924; i--) {
                $(this).append($('<option>', {
                    value: i,
                    text: i
                }));
            }
        }
    });

    $('#birth-month').on('focus', function() {
        if (!isMonthOptionExisted) {
            isMonthOptionExisted = true;
            for (let i = 1; i <= 12; i++) {
                $(this).append($('<option>', {
                    value: i,
                    text: i
                }));
            }
        }
    });

    $('#birth-day').on('focus', function() {
        if (!isDayOptionExisted) {
            isDayOptionExisted = true;
            for (let i = 1; i <= 31; i++) {
                $(this).append($('<option>', {
                    value: i,
                    text: i
                }));
            }
        }
    });
});
