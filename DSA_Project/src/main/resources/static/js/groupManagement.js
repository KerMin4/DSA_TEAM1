document.addEventListener('DOMContentLoaded', function() {
    const dropdownButtons = document.querySelectorAll('.dropdown-btn');
    
    dropdownButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            event.stopPropagation(); // 버튼 클릭 시 이벤트가 상위로 전파되지 않도록 함
            const dropdownContent = button.nextElementSibling;
            dropdownContent.style.display = (dropdownContent.style.display === 'block') ? 'none' : 'block';
        });
    });
    
    // 다른 곳을 클릭했을 때 드롭다운을 닫음
    document.addEventListener('click', function(event) {
        dropdownButtons.forEach(function(button) {
            const dropdownContent = button.nextElementSibling;
            if (dropdownContent.style.display === 'block') {
                dropdownContent.style.display = 'none';
            }
        });
    });
    
    // 삭제 확인 기능
    const deleteButtons = document.querySelectorAll('.delete-btn');
    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            const confirmed = confirm('정말로 이 그룹을 삭제하시겠습니까?');
            if (!confirmed) {
                event.preventDefault();
            }
        });
    });
});
