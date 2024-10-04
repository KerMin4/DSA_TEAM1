  $(document).ready(function() {
            // 페이지 로드 시 currentStep 가져오기
            const currentStep = sessionStorage.getItem('currentStep') || '1';
            showStep(currentStep); // 가져온 currentStep으로 스텝 표시

            const formData = {};

            function showStep(step) {
                $('.step').hide();  
                $('#step' + step).show();  
                updateStepper(step);  
            }

            function validateStep1() {
                var selectedInterests = $('input[name="interests"]:checked').length;
                console.log('선택한 취향 개수:', selectedInterests); 

                if (selectedInterests === 0) {
                    alert("최소한 하나의 취향을 선택해 주세요.");
                    return false;
                } else if (selectedInterests > 5) {
                    alert("취향은 다섯 개까지 선택 가능해요.");
                    return false;
                }
                return true;
            }

            function validateStep2() {
                var location = $('#location').val();
                if (!location) {
                    alert("관심 지역을 입력해 주세요.");
                    return false;
                }
                return true;
            }

            $('#nextStep1').click(function(e) {
                e.preventDefault(); 
                console.log('스텝 1에서 다음 버튼 클릭'); 

                if (validateStep1()) {
                    var selectedInterests = [];
                    $('input[name="interests"]:checked').each(function() {
                        selectedInterests.push($(this).val());
                    });
                    formData.interests = selectedInterests;
                    console.log('스텝 1에서 선택한 취향:', selectedInterests); 
                    sessionStorage.setItem('currentStep', '2'); // 스텝 2로 이동
                    showStep(2);  // 스텝 2 화면 표시
                }
            });

            $('#prevStep1').click(function(e) {
                e.preventDefault(); 
                console.log('스텝 1에서 이전 버튼 클릭, 홈으로 이동'); 
                window.location.href = '/kkirikkiri/';
            });

            $('#nextStep2').click(function(e) {
                e.preventDefault(); 
                console.log('스텝 2에서 다음 버튼 클릭'); 

                if (validateStep2()) {
                    var location = $('#location').val();
                    formData.location = location;
                    console.log('스텝 2에서 입력한 위치:', location); 
                    sessionStorage.setItem('currentStep', '3'); 
                    showStep(3); 
                }
            });

            $('#prevStep2').click(function(e) {
                e.preventDefault(); 
                console.log('스텝 2에서 이전 버튼 클릭');
                sessionStorage.setItem('currentStep', '1'); 
                showStep(1);  
            });

            $('#prevStep3').click(function(e) {
                e.preventDefault(); 
                console.log('스텝 3에서 이전 버튼 클릭');
                sessionStorage.setItem('currentStep', '2'); 
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

            $('#submitForm').click(function(e) {
                e.preventDefault(); 
                console.log('가입하기 버튼 클릭'); 

                var userid = $('#userid').val();
                var password = $('#password').val();
                var password2 = $('#password2').val();
                var phone = $('#phoneNumber').val();
                var email = $('#email').val();

                if (!userid || !password || !password2 || !phone || !email) {
                    alert("모든 정보를 입력해 주세요.");
                    return;
                }

                if (password !== password2) {
                    alert("비밀번호가 일치하지 않습니다.");
                    return;
                }

                formData.userid = userid;
                formData.password = password;
                formData.phone = phone;
                formData.email = email;

                console.log('가입할 데이터:', formData);  
                // 여기에 가입 처리 로직 추가
            });

            $('input[name="interests"]').change(function() {
                var selectedInterests = $('input[name="interests"]:checked').length;
                console.log('현재 선택한 취향 개수:', selectedInterests); 
                if (selectedInterests > 5) {
                    alert("취향은 다섯 개까지 선택 가능해요.");
                    $(this).prop('checked', false);
                }

                $('.interest-text').css('background-color', '#FFFFFF').css('color', '#000000');
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
                sessionStorage.setItem('currentStep', step); 
                showStep(step);
            });
        });