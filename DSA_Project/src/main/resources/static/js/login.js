/**
 * 
 */
$(function(){
	$('#loginForm').submit(function(){
		if($('#id').value == '' || $('#password').val() == ''){
			alert('ID와 비밀번호를 입력하세요');
			return false;
		}
		return true;
	});
});