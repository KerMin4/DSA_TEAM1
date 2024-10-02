$(function() {
    // Tab click event handler
    $('.tabs a').on('click', function(event) {
        event.preventDefault();
        
        // Get the content ID from the data attribute
        const contentToShow = $(this).data('content');
        
        // Hide all content sections
        $('.content-section').addClass('hidden');
        
        // Show the selected content section
        $('#' + contentToShow).removeClass('hidden');
    });
});
