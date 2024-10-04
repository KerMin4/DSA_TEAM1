$(function() {
    // 그룹 필터링을 위한 함수
    function filterGroups() {
        var searchQuery = $('#searchInput').val().trim();  // 검색어
        var selectedInterest = $('input[name="interest"]:checked').val();  // 선택된 카테고리
        var selectedRegion = $('.sub-region-btn.selected').data('subregion');  // 선택된 지역

        // selectedRegion이 undefined일 경우 처리
        if (!selectedRegion) {
            selectedRegion = '';
        }

        // Ajax 요청으로 검색 및 필터링 실행
        $.ajax({
		    url: 'http://localhost:7272/api/filter',
		    type: 'GET',
		    data: {
		        query: searchQuery,
		        category: selectedInterest,
		        region: selectedRegion
		    },
		    success: function(response) {
		        console.log('서버 응답:', response);  // 서버 응답 확인 로그
		
		        if (response.html.trim() === '') {
		            // 검색 결과가 없을 때 메시지를 보여줌
		            $('#no-result-message').show();
		            $('#no-result-text').text(`"${searchQuery}"에 대한 검색 결과를 찾을 수 없습니다.`);
		            $('.group-listing').html('');
		        } else {
		            // 검색 결과가 있을 때, 목록을 업데이트하고 메시지를 숨김
		            $('#no-result-message').hide();
		            $('.group-listing').html(response.html);
		        }
		    },
		    error: function(err) {
		        console.error('필터링 중 오류 발생:', err);
		    }
		});
    }

    // 검색 버튼 클릭 이벤트
    $('#searchButton').on('click', function(event) {
        event.preventDefault();
        filterGroups();  // 필터링 함수 호출
    });

    // 카테고리 선택 이벤트
    $('input[name="interest"]').on('change', function() {
        filterGroups();  // 필터링 함수 호출
    });

    // 지역 필터링 이벤트 처리
    $('#region-container').on('click', '.sub-region-btn', function() {
        // 선택한 지역 버튼 스타일 처리
        $('.sub-region-btn').removeClass('selected');
        $(this).addClass('selected');
        filterGroups();  // 필터링 함수 호출
    });

    // 카테고리 제목 클릭 시 카테고리 목록 보이기/숨기기
    $('#category-toggle').on('click', function() {
        $('#category-container').toggle();
    });

    // 지역별 필터 제목 클릭 시 필터 목록 보이기/숨기기
    $('#location-toggle').on('click', function() {
        $('#location-container').toggle();
    });

    // 상위 지역 데이터 및 하위 지역 버튼 생성
    const regions = {
        "서울": ["서울 전체", "강남", "강동", "강북", "강서", "관악", "광진", "구로", "금천", "노원", "도봉", "동대문", "동작", "마포", "서대문", "서초", "성동", "성북", "송파", "양천", "영등포", "용산", "은평", "종로", "중구", "중랑"],
	    "인천": ["인천 전체", "중구", "동구", "미추홀", "연수", "남동", "부평", "계양구", "서구", "강화", "옹진"],
	    "경기": ["경기 전체", "수원", "성남", "고양", "용인", "부천", "안산", "안양", "남양주", "화성", "의정부", "시흥", "평택", "광명", "파주", "군포", "광주", "김포", "이천", "양주", "구리", "오산", "안성", "의왕", "하남", "포천", "동두천", "과천", "여주", "양평", "가평", "연천"],
	    "강원": ["강원 전체", "춘천", "인제", "양구", "고성", "양양", "강릉", "속초", "삼척", "정선", "평창", "영월", "원주", "횡성", "홍천", "화천", "철원", "동해", "태백"],
	    "충북": ["충북 전체", "청주", "충주", "제천", "보은", "옥천", "영동", "증평", "진천", "괴산", "음성", "단양"],
	    "충남": ["충남 전체", "천안", "공주", "보령", "아산", "서산", "논산", "계룡", "당진", "금산", "부여", "서천", "청양", "홍성", "예산", "태안"],
	    "세종": ["세종 전체", "조치원", "연기", "연동", "부강", "금남", "장군", "연서", "전의", "전동", "소정", "한솔", "새롬", "도담", "아름", "종촌", "고운", "소담", "보람", "대평", "다정", "해밀", "반곡"],
	    "대전": ["대전 전체", "동구", "중구", "서구", "유성구", "대덕구"],
	    "광주": ["광주 전체", "광산구", "남구", "동구", "북구", "서구"],
	    "전북": ["전북 전체", "전주", "익산", "군산", "정읍", "남원", "김제", "완주", "고창", "부안", "임실", "순창", "진안", "무주", "장수"],
	    "전남": ["전남 전체", "목포", "여수", "순천", "나주", "광양", "담양", "곡성", "구례", "고흥", "보성", "화순", "장흥", "강진", "해남", "영암", "무안", "함평", "영광", "장성", "완도", "진도", "신안"],
	    "경북": ["경북 전체", "포항", "경주", "김천", "안동", "구미", "영주", "영천", "상주", "문경", "경산", "군위", "의성", "청송", "영양", "영덕", "청도", "고령", "성주", "칠곡", "예천", "봉화", "울진", "울릉"],
	    "경남": ["경남 전체", "창원", "김해", "양산", "진주", "거제", "통영", "사천", "밀양", "함안", "거창", "창녕", "고성", "하동", "합천", "남해", "함양", "산청", "의령"],
	    "대구": ["대구 전체", "중구", "동구", "서구", "남구", "북구", "수성구", "달서구", "달성군"],
	    "울산": ["울산 전체", "남구", "동구", "북구", "중구", "울주군"],
	    "부산": ["부산 전체", "중구", "서구", "동구", "영도구", "부산진구", "동래구", "남구", "북구", "강서구", "해운대구", "사하구", "금정구", "연제구", "수영구", "사상구", "기장군"],
	    "제주": ["제주 전체", "제주", "한림", "애월", "구좌", "조천", "한경", "추자", "우도", "서귀포", "대정", "남원", "성산", "안덕", "표선"]
    };

    // 상위 지역 버튼 생성
    const largeRegions = Object.keys(regions);
    largeRegions.forEach(region => {
        $('#region-container').append(`<button class="region-btn" data-region="${region}">${region}</button>`);
    });

    // 상위 지역 버튼 클릭 시 세부 지역 표시 및 다른 지역 접기
    $('#region-container').on('click', '.region-btn', function() {
        const regionKey = $(this).data('region');
        const subRegions = regions[regionKey];

        // 다른 열려있는 모든 하위 지역들을 숨김
        $('.sub-region-list').not($(this).next('.sub-region-list')).slideUp();

        // 세부 지역 버튼이 이미 있는지 확인하고, 있으면 숨기거나 다시 보여줌
        if ($(this).next('.sub-region-list').length) {
            $(this).next('.sub-region-list').slideToggle();  // slideToggle() for smooth hide/show
        } else {
            const subRegionContainer = $('<div class="sub-region-list"></div>');
            subRegions.forEach(subRegion => {
                subRegionContainer.append(`<button class="sub-region-btn" data-subregion="${subRegion}">${subRegion}</button>`);
            });
            $(this).after(subRegionContainer);
            subRegionContainer.slideDown();  // Smooth reveal
        }
    });

    // 정렬 기준 토글
    $('#sort-toggle').on('click', function() {
        $('#sort-options').toggle();
    });
    
});
