$(document).ready(function(){
   
    $('#profileImage').click(function(){
        $('#profileImageUpload').click(); 
    });

   
    $('#profileImageUpload').change(function(){
        var file = this.files[0];
        if (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#profileImage').attr('src', e.target.result); 
            }
            reader.readAsDataURL(file); 
        }
    });

    
    $('#submitProfileImage').click(function(){
      
        alert('프로필 이미지가 수정되었습니다.');
    });
});
