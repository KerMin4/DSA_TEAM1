var markers = [];

var mapContainer = document.getElementById('map'), 
    mapOption = {
        center: new kakao.maps.LatLng(37.566826, 126.9786567), 
        level: 3 
    };  

var map = new kakao.maps.Map(mapContainer, mapOption); 

var ps = new kakao.maps.services.Places();  

var infowindow = new kakao.maps.InfoWindow({zIndex:1});

searchPlaces();

function searchPlaces() {

    var keyword = document.getElementById('keyword').value;

    if (!keyword.replace(/^\s+|\s+$/g, '')) {
        alert('키워드를 입력해주세요!');
        return false;
    }

    ps.keywordSearch( keyword, placesSearchCB); 
}

function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {

        displayPlaces(data);

        displayPagination(pagination);

    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {

        alert('검색 결과가 존재하지 않습니다.');
        return;

    } else if (status === kakao.maps.services.Status.ERROR) {

        alert('검색 결과 중 오류가 발생했습니다.');
        return;

    }
}

function displayPlaces(places) {

    var listEl = document.getElementById('placesList'), 
    menuEl = document.getElementById('menu_wrap'),
    fragment = document.createDocumentFragment(), 
    bounds = new kakao.maps.LatLngBounds(), 
    listStr = '';
    
    removeAllChildNods(listEl);

    removeMarker();
    
    for ( var i=0; i<places.length; i++ ) {

        var placePosition = new kakao.maps.LatLng(places[i].y, places[i].x),
            marker = addMarker(placePosition, i), 
            itemEl = getListItem(i, places[i]); 

        bounds.extend(placePosition);

        (function(marker, title) {
            kakao.maps.event.addListener(marker, 'mouseover', function() {
                displayInfowindow(marker, title);
            });

            kakao.maps.event.addListener(marker, 'mouseout', function() {
                infowindow.close();
            });

            itemEl.onmouseover =  function () {
                displayInfowindow(marker, title);
            };

            itemEl.onmouseout =  function () {
                infowindow.close();
            };
        })(marker, places[i].place_name);

        fragment.appendChild(itemEl);
    }

    listEl.appendChild(fragment);
    menuEl.scrollTop = 0;

    map.setBounds(bounds);
}
/*
function getListItem(index, places) {

    var el = document.createElement('li'),
    itemStr = '<span class="markerbg marker_' + (index+1) + '"></span>' +
                '<div class="info">' +
                "   <a id = 'placeInfo' >" + places.place_name + '</a>'
                		/*+ "<a href = '${`https://map.kakao.com/link/map/" + places.id +"`}' class = 'flex items-center gap-4'>"
                		+ "<a href= '${`https://map.kakao.com/link/map/${places.id}`}' class = 'flex items-center gap-4'"
                		+ "<i class = 'ph-fill ph-map-pin text-28 block text-primary'></i>"
                		+ '<div>'
                			+ "<p class = 'font-medium'>" + `${places.place_name}` + '</p>'
                			+ "<p class = 'text-14 text-zinc-400'>" + places.address+'</p>'
                			+ '</div>'
                			+'</a>' 여기에 주석 추가/
                ;

    if (places.road_address_name) {
        itemStr += '    <span>' + places.road_address_name + '</span>' +
                    '   <span class="jibun gray">' +  places.address_name  + '</span>';
    } else {
        itemStr += '    <span>' +  places.address_name  + '</span>'; 
    }
                 
      itemStr += '  <span class="tel">' + places.phone  + '</span>' +
                '</div>';           

    el.innerHTML = itemStr;
    el.className = 'item';

    return el;
}
*/

function getListItem(index, places) {
    var el = document.createElement('li'),
        itemStr = `
            <span class="markerbg marker_${index + 1}"></span>
            <div class="info">
                <a id="placeInfo" href="#" class="place-link">
                    ${places.place_name}
                </a>
            </div>
        `;

    if (places.road_address_name) {
        itemStr += `
            <span class="jibun gray">${places.road_address_name}</span>
            <span class="jibun gray">${places.address_name}</span>
        `;
    } else {
        itemStr += `<span>${places.address_name}</span>`;
    }

    itemStr += `<span class="tel">${places.phone}</span>
 	<button class = "transmitBtn" id = "transmitBtn">확인</button>`;

    el.innerHTML = itemStr;
    el.className = 'item';

    el.querySelector('.place-link').addEventListener('click', function (e) {
        e.preventDefault();
        const url = `https://place.map.kakao.com/${places.id}`;
        window.open(url, '_blank'); 
    });

	el.querySelector('.transmitBtn').addEventListener('click', function(){
		sendDataToServer(places);
	});
    return el;
}

function sendDataToServer(places){
	var place= places.place_name + " " + places.address_name; 
	
	$(opener.document).find('#location').val(place);
	window.close();
}
function addMarker(position, idx, title) {
    var imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', 
        imageSize = new kakao.maps.Size(36, 37),  
        imgOptions =  {
            spriteSize : new kakao.maps.Size(36, 691), 
            spriteOrigin : new kakao.maps.Point(0, (idx*46)+10), 
            offset: new kakao.maps.Point(13, 37)
        },
        markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
            marker = new kakao.maps.Marker({
            position: position, 
            image: markerImage 
        });

    marker.setMap(map); 
    markers.push(marker); 

    return marker;
}

function removeMarker() {
    for ( var i = 0; i < markers.length; i++ ) {
        markers[i].setMap(null);
    }   
    markers = [];
}

function displayPagination(pagination) {
    var paginationEl = document.getElementById('pagination'),
        fragment = document.createDocumentFragment(),
        i; 

    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild (paginationEl.lastChild);
    }

    for (i=1; i<=pagination.last; i++) {
        var el = document.createElement('a');
        el.href = "#";
        el.innerHTML = i;

        if (i===pagination.current) {
            el.className = 'on';
        } else {
            el.onclick = (function(i) {
                return function() {
                    pagination.gotoPage(i);
                }
            })(i);
        }

        fragment.appendChild(el);
    }
    paginationEl.appendChild(fragment);
}

function displayInfowindow(marker, title) {
    var content = '<div style="padding:5px;z-index:1;">' + title + '</div>';

    infowindow.setContent(content);
    infowindow.open(map, marker);
}

function removeAllChildNods(el) {   
    while (el.hasChildNodes()) {
        el.removeChild (el.lastChild);
    }
}

$(document).ready(function(){
	$('#placeInfo').click(function(){
		window.open(url);
	});
});