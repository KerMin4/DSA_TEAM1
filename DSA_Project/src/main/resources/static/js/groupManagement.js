document.addEventListener('DOMContentLoaded', function() {
    const dropdownContents = document.querySelectorAll('.dropdown-content');
    dropdownContents.forEach(function(content) {
        content.style.display = 'none';
    });

    const dropdownButtons = document.querySelectorAll('.dropdown-btn');
    dropdownButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            event.stopPropagation();

            dropdownContents.forEach(function(content) {
                content.style.display = 'none';
            });

            const dropdownContent = button.nextElementSibling;
            dropdownContent.style.display = 'block';
        });
    });

    document.addEventListener('click', function() {
        dropdownContents.forEach(function(content) {
            content.style.display = 'none';
        });
    });

  
    const deleteButtons = document.querySelectorAll('.delete-btn');
    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            const confirmed = confirm('정말로 이 그룹을 삭제하시겠습니까?');
            if (!confirmed) { 
                event.preventDefault();
            }
        });
    });

 
    const leaveButtons = document.querySelectorAll('.leave-btn');
    leaveButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            const confirmed = confirm('정말로 이 그룹에서 탈퇴하시겠습니까?');
            if (!confirmed) { 
                event.preventDefault();
            }
        });
    });
});
