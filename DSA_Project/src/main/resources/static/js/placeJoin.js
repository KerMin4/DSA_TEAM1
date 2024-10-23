/**
 * join-btn
 * 플레이스 참여 모달 창
 */

 // 모달 관련 스크립트
const modal = document.getElementById("modal");
const closeButton = document.getElementsByClassName("close")[0];

// 모든 "참가하기" 버튼에 이벤트 리스너 추가
document.querySelectorAll('.join-btn').forEach(button => {
    button.addEventListener('click', function(event) {
        event.preventDefault(); // 기본 링크 클릭 동작 방지
        const placeId = this.closest('.place-card').getAttribute('data-place-id');
        loadPlaceDetails(placeId);
    });
});

// 플레이스 정보 로드 함수
function loadPlaceDetails(placeId) {
	
	// placeId가 유효한지 확인
    if (!placeId || isNaN(placeId)) {
        console.error('유효하지 않은 placeId:', placeId);
        return; // 유효하지 않으면 함수 종료
    }
	
    // 서버에서 플레이스 정보를 가져오는 AJAX 요청
    fetch(`/kkirikkiri/place/joinPlace/${placeId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('네트워크 응답이 좋지 않습니다.');
            }
            return response.json();
        })
        .then(data => {
            // 모달에 데이터 설정
            document.getElementById('placeTitle').textContent = data.title;
            document.getElementById('placeDescription').textContent = data.description;
            document.getElementById('placeCategory').textContent = data.category;
            document.getElementById('placeLocation').textContent = data.location;
            document.getElementById('placeEventDate').textContent = data.eventDate;
            document.getElementById('placeRequiredMembers').textContent = data.requiredMembers;
            document.getElementById('placeCurrentMembers').textContent = data.currentMembers;
            document.getElementById('placeMemberLimit').textContent = data.memberLimit;
            document.getElementById('placeProfileImage').src = '/kkirikkiri/images/' + data.profileImage;
            document.getElementById('placeViewCount').textContent = data.viewCount;
            document.getElementById('placeBookmarkCount').textContent = data.bookmarkCount;
            document.getElementById('placePrice').textContent = data.price;
            document.getElementById('placeVendor').textContent = data.vendor;
            document.getElementById('placeCreatedAt').textContent = data.createdAt;

            // 모달 열기
            modal.style.display = "block";
        })
        .catch(error => {
            console.error('문제가 발생했습니다:', error);
        });
}

// 모달 닫기
// 닫기 버튼이 존재하는지 확인
if (closeButton) {
    closeButton.onclick = function() {
        modal.style.display = "none";
    };
} else {
    console.error('닫기 버튼을 찾을 수 없습니다.');
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}
