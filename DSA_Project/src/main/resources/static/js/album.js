$(function() {
    
    // 모달을 열기 위한 버튼 클릭 이벤트
    $('#openAlbumModal').on('click', function() {
        $('#albumModal').show(); // 모달을 보이게 함
    });

    // 모달 닫기 버튼 클릭 이벤트
    $('.close').on('click', function() {
        $('#albumModal').hide(); // 모달을 숨김
    });

    // 모달 외부 클릭 시 모달 닫기
    $(window).on('click', function(event) {
        if ($(event.target).is('#albumModal')) {
            $('#albumModal').hide(); // 모달을 숨김
        }
    });

    // 이미지 파일 선택 시 미리보기
    $('#modalAlbumImageInput').on('change', function() {
        var file = this.files[0];
        if (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#modalImagePreview').attr('src', e.target.result).show(); // 모달 내 미리보기 설정
            };
            reader.readAsDataURL(file); // 이미지 파일 읽기
        }
    });

    // 이미지 업로드 버튼 클릭 시 이미지 앨범에 추가
    $('#modalUploadAlbumImage').on('click', function() {
        var file = $('#modalAlbumImageInput')[0].files[0];
        if (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                const imageSrc = e.target.result;

                // 새로운 사진을 앨범 그리드에 추가
                const photoHTML = `
                    <div class="uploaded-photo">
                        <img src="${imageSrc}" alt="Uploaded Photo">
                        <div class="album-actions">
                            <button class="deletePhoto">삭제</button>
                        </div>
                    </div>`;
                $('#uploadedPhotos').append(photoHTML);

                // 모달 초기화 및 닫기
                $('#modalImagePreview').hide();
                $('#modalAlbumImageInput').val('');
                $('#albumModal').hide();
            };
            reader.readAsDataURL(file); // 이미지 파일 읽기
        }
    });

    // 취소 버튼 클릭 시 모달 닫기
    $('#modalCancelUpload').on('click', function() {
        $('#albumModal').hide(); // 모달 닫기
    });

    // 사진 삭제 기능
    $('#uploadedPhotos').on('click', '.deletePhoto', function() {
        $(this).closest('.uploaded-photo').remove();
    });
});
