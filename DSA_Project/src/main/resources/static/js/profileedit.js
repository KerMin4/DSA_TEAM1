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
    });