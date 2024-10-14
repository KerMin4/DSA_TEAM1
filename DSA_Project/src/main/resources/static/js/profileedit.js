$(document).ready(function() {
   
    $('#editProfileButton').click(function() {
        $('#profileImageInput').click();
    });

   
    $('#profileImageInput').change(function(event) {
        var input = event.target;
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
               
                $('#profileEditImg').attr('src', e.target.result); 
            }
            reader.readAsDataURL(input.files[0]); 
        }
    });

   
    $('#submitProfileChange').click(function(event) {
      
        var profileImageInput = $('#profileImageInput').val();
        
        if (!profileImageInput) {
            event.preventDefault();
            alert("프로필 사진을 새로 설정해주세요."); 
        } else {
            $(this).closest('form').submit();
        }
    });
});
