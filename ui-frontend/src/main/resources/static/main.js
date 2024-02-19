$(function(){
    
    const defaultHeaders = {'Content-Type': "application/json"};
    let basePath = window?.API_BASE_URL;

    if (!basePath) {
      basePath = window.location.protocol + "//" + window.location.host;
    }

    $.ajax({
        url: `${basePath}/api/fruits`,
        headers: defaultHeaders,
        dataType: "json",
    }).then(function(data) {
        console.log(data);
        let data_stringify = JSON.stringify(data);
        let data_json = JSON.parse(data_stringify);
        $.each(data_json, function(index, value) {
            $('.scene').append('<div>'+ value.id + ':' + value.name + '</div>');
        })
    });

    $("#upButton").on('click', function () {
        console.log("upButton clicked.");
        let data = new FormData($("#upform").get(0));
        $.ajax({
            url:`${basePath}/api/git`,
            data: data,
            cache: false,
            contentType: false,
            processData: false,
            method: 'POST'
        });
    });
    
    $("#commitButton").on('click', function () {
        console.log("commitButton clicked.");
        let data = new FormData($("#commitform").get(0));
        $.ajax({
            url:`${basePath}/api/git/commit`,
            data: data,
            cache: false,
            contentType: false,
            processData: false,
            method: 'POST'
        });
    });
});