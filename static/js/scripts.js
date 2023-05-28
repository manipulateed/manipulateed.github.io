
$(document).ready(function(){

    $("#collapse").on("click", function(){

        $("#bar").toggleClass("active");
        $(".fa-hand-point-right").toggleClass("fa-align-left");
    }) 
})

function con(){
    confirm("是否滿18歲?");
}
