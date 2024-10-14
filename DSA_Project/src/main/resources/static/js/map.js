$(document).ready(function()
{
	var infowindow = new kakao.maps.InfoWindow({zIndex:1});
	var placeSearch = new kakao.maps.services.Places();
	$('#searchBtn').click(function(){
		var searchPlace = $('#searchPlace').val();
		console.log("찾는 거:", searchPlace);
		placeSearch.keywordSearch(searchPlace, placesSearchCB);
	});
	
	function placesSearchCB (data, status, pagination) {
   		 if (status === kakao.maps.services.Status.OK) {

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해
        // LatLngBounds 객체에 좌표를 추가합니다
        var bounds = new kakao.maps.LatLngBounds();
		console.log("두번째",bounds);
        for (var i=0; i<data.length; i++) {
            displayMarker(data[i]);    
            bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
        }       

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
        map.setBounds(bounds);
    } 
}

	function displayMarker(place) {
    
   		 // 마커를 생성하고 지도에 표시합니다
   		 var marker = new kakao.maps.Marker({
       		 map: map,
       	 position: new kakao.maps.LatLng(place.y, place.x) 
    });

    	 // 마커에 클릭이벤트를 등록합니다
   		 kakao.maps.event.addListener(marker, 'click', function() {
         // 마커를 클릭하면 장소명이 인포윈도우에 표출됩니다
        	infowindow.setContent('<div style="padding:5px;font-size:12px;">' + place.place_name + '</div>');
       	    infowindow.open(map, marker);
    });
}
});

/*var container = document.getElementById('map');
		var options = {
			center: new kakao.maps.LatLng(33.450701, 126.570667),
			level: 3
		};

		var map = new kakao.maps.Map(container, options);
	window.onload = function () {
    // 카카오 맵 초기화
    const mapContainer = document.getElementById('map');
    if (mapContainer) {
        const mapOptions = {
            center: new kakao.maps.LatLng(33.450701, 126.570667), // 기본 중심 좌표
            level: 3 // 확대 레벨
        };
        const map = new kakao.maps.Map(mapContainer, mapOptions); // 지도 생성

        // 사용자 위치 선택 기능 추가 (필요한 경우)
        document.getElementById('location').addEventListener('input', function() {
            const location = this.value; // 입력된 위치
            // 위치 검색 기능 (주소로 좌표 변환) 추가 가능
            // 예: 카카오 API로 위치 검색 후 지도의 중심 좌표 변경
        });
    } else {
        console.error("Map container not found!");
    }
}*/
