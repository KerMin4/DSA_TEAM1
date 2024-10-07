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

			if(selectedInterests.length > 5){
				alert("취향은 다섯 개 까지 선택가능해요.");
				selectedInterests = [];
				return false;
				//updating 				
			}
			showStep(2);
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

		$('#submitForm').click(function(){
			var userid = $('#userid').val();
			var password = $('#password').val();
			var password2 = $('#password2').val();
			var phone = $('#phone').val();
			var email = $('#email').val();

			if(!userid || !password || !password2 || !phone || !email){
				alert("모든 정보를 입력해 주세요.");
				return;
			}

			if(password !== password2){
				alert("비밀번호가 일치하지 않습니다.");
				return;
			}

			formData.userid = userid;
			formData.password = password;
			formData.phone = phone;
			formData.email = email;
			
			alert(formData);
			console.log(formData);
			console.log(formData.interests);
			console.log(formData.location);
			$.post("/kkirikkiri/member/join1", formData, function(response){
				console.log("여기까진 옴?");
				window.location.href= "/kkirikkiri";
			}).fail(function(error){
				console.log(formData);
				console.log("실패다 이자식아");
			});
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
                }

                $('.interest-text').css('background-color', '#FFFFFF').css('color', '#000000');3
                $('input[name="interests"]:checked').each(function() {
                    $(this).siblings('.interest-text').css('background-color', '#F8B1A3').css('color', '#000000');
                });
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
	});