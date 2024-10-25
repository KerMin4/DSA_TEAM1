
/*      
document.getElementById("joinForm").onsubmit = function() {
    if (!document.getElementById("infoAgreement").checked) {
        alert("개인정보 처리 방침에 동의해야 합니다.");
        return false;
    }
    if (!document.getElementById("paymentAgreement").checked) {
        alert("결제 약관에 동의해야 합니다.");
        return false;
    }
};
*/

$(document).ready(function() {
    $("#joinForm").submit(function() {
        if (!$("#infoAgreement").is(":checked")) {
            alert("개인정보 처리 방침에 동의해야 합니다.");
            return false;
        }
        if (!$("#paymentAgreement").is(":checked")) {
            alert("결제 약관에 동의해야 합니다.");
            return false;
        }
    });
});

function redirectToPlaceMain() {
        window.location.href = /*[[@{/place/placeMain}]]*/'';
    }
    