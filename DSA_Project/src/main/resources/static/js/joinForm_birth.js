/*const birthYearEl = document.querySelector('#birth-year');

let isYearOptionExisted = false;
birthYearEl.addEventListener('click', function(){
	alert('이거 인식');
	if(!isYearOptionExisted){
		isYearOptionExisted = true;
		for(var i = 1924; i<=2024; i++){
			const YearOption = document.createElement('option')
			YearOption.setAttribute('value', i);
			YearOption.innerText = i;
			this.appendChild(YearOption);
		}
	}
});

const birthMonthEl = document.querySelector('#birth-month');

let isMonthOptionExisted = false;
birthMonthEl.addEventListener('click', function(){
	if(!isMonthOptionExisted){
		isMonthOptionExisted = true;
		for(var i = 1; i<=12; i++){
			const MonthOption = document.createElement('option');
			MonthOption.setAttribute('value', i);
			MonthOption.innerText = i;
			this.appendChild(MonthOption);
		}
	}
});

const birthDayEl = document.querySelector('#birth-day');

let isDayOptionExisted = false;
birthDayEl.addEventListener('click', function(){
	if(!isDayOptionExisted){
		isDayOptionExisted = true;
		for(var i = 1; i<=31; i++){
			const DayOption = document.createElement('option');
			DayOption.setAttribute('value', i);
			DayOption.innerText = i;
			this.appendChild(DayOption);
		}
	}
});
*/