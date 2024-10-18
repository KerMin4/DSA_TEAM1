document.addEventListener("DOMContentLoaded", function() {
    const profileImg = document.getElementById("profileImg");
    const profileDropdown = document.getElementById("profileDropdown");

   
    profileImg.addEventListener("click", function(event) {
        event.stopPropagation(); 
        profileDropdown.classList.toggle("show"); 
    });

 
    document.addEventListener("click", function(event) {
        if (!profileDropdown.contains(event.target) && !profileImg.contains(event.target)) {
            profileDropdown.classList.remove("show"); 
        }
    });
});
