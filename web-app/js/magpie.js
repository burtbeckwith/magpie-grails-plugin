

var errands = new Array()

function prepareConsole() {
    $('#errandsDisplay').hide()
    requestErrands()
}



function requestErrands() {

    $.ajax({
        url: '/magpie/restfulMagpie/errands',
        data: null,
        success: onReceivingErrands,
        dataType: 'json'
    });
}



function onReceivingErrands(data){
    errands = data
    populateErrandsDisplay()
    requestFetchesForErrands()
}

function requestFetchesForErrands() {

    for(var x=0; x < errands.length;x++){
        var errand = errands[x]
        requestFetchesForErrand(errand.id)
    }
}

function populateErrandsDisplay(){

    var $errands = $('#errands')

    for(var x=0; x < errands.length;x++){
        var errand = errands[x]

        var errandRow = ''

        var errandRowId = "errandRow_" + errand.id

        errandRow += "<tr class='errandRow' id='#" + errandRowId + "'>"

        errandRow += "<td>" + errand.name + "</td>"


        var displayedUrl = "<a href='" + errand.url + "' target='_external'>" + 'URL' + "</a>"

        errandRow += "<td>" + displayedUrl + "</td>"
        errandRow += "<td>" + errand.cronExpression + "</td>"

        var displayedEnforcedContentTypeForRendering = errand.enforcedContentTypeForRendering ? errand.enforcedContentTypeForRendering : ''
        errandRow += "<td>" + displayedEnforcedContentTypeForRendering + "</td>"

        var displayedActive = errand.active ? 'Yes' : 'No'
        errandRow += "<td>" + displayedActive + "</td>"

        errandRow += "</tr>"

        var errandFetchesRowId = "errandFetchesRow_" + errand.id
        errandRow += "<tr id='" + errandFetchesRowId + "'>"

        var manualFetchErrandId = 'errandManualFetch_' + errand.id
        errandRow += "<td align='center' valign='center'><input type='button' class='manualFetch' id='" + manualFetchErrandId + "' value='Fetch Now'></td>"
        errandRow += "<td colspan=5>"

        var errandFetchesTableId = "errandFetchesTable_" + errand.id
        errandRow += "<table id='" + errandFetchesTableId + "' width='100%' cellpadding='2' cellspacing='0' border='1'>"
        errandRow += "</table>"

        errandRow += "</td>"
        errandRow += "</tr>"


        $errands.append(errandRow)

    }

    $('.manualFetch').mousedown(function(event) {whenManualFetchRequested(event)})

    $('#errandsDisplay').show()
}

function requestNewFetchForErrand(errandId) {

    log('requestNewFetchForErrand ...!' + errandId)

    var url = '/magpie/restfulMagpie/errands/' + errandId + '/fetches'

    $.ajax({
        method:'POST',
        url: url,
        data: null,
            statusCode:
            {
                201: function(){onHavingSuccessfullyFetchedForErrand(errandId);}
            },
        dataType: 'json'
    });
}

function onHavingSuccessfullyFetchedForErrand(errandId){
    log('onHavingSuccessfullyFetchedForErrand' + errandId)

    $('#' + 'errandFetchesRow_' + errandId + ' .fetchDataRow').remove()

    requestFetchesForErrand(errandId)
}

function whenManualFetchRequested(event){

    var regex = /_(\d+)$/
    var match = regex.exec(event.delegateTarget.id)
    var errandId = match[1]

    log("ErrandId:" + errandId)

    requestNewFetchForErrand(errandId)

}


function requestFetchesForErrand(errandId) {

    log('requestFetchesForErrand ....' + errandId)

     var url = '/magpie/restfulMagpie/errands/' + errandId + '/fetches'

    $.ajax({
        url: url,
        data: null,
        success: function(data) {onReceivingFetchesForErrand(errandId,data);},
        dataType: 'json'
    });

}


function onReceivingFetchesForErrand(errandId,data){

    var fetches = data

    log("Errand " + errandId + " has " + fetches.length + " Fetches ...")

    var $errandFetchesTable = $('#errandFetchesTable_' + errandId)

    var headerForFetchesForErrand = ''

    headerForFetchesForErrand += "<tr bgcolor='#add8e6'>"
    headerForFetchesForErrand += "<td width='30%'>Date</td>"
    headerForFetchesForErrand += "<td width='20%'>Status</td>"
    headerForFetchesForErrand += "<td width='30%'>Content Type</td>"
    headerForFetchesForErrand += "<td width='10%'>Content Size</td>"
    headerForFetchesForErrand += "</tr>"


    var errandFetchesDisplay = headerForFetchesForErrand

    for(var x=0; x < fetches.length;x++){
        var fetch = fetches[x]



        var fetchId         = fetch.id
        var errandId        = fetch.errandId
        var date            = fetch.date
        var httpStatusCode  = fetch.httpStatusCode
        var contentType     = fetch.contentType
        var contentSize     = fetch.contentSize

        errandFetchesDisplay += "<tr class='fetchDataRow'>"
        errandFetchesDisplay += '<td>' + date + '</td>'
        errandFetchesDisplay += '<td>' + httpStatusCode + '</td>'
        errandFetchesDisplay += '<td>' + contentType + '</td>'
        errandFetchesDisplay += '<td>' + contentSize + '</td>'
        errandFetchesDisplay += '</tr>'


    }


    $errandFetchesTable.html(errandFetchesDisplay)


}

/**
 * TODO: remember IE8
 */
function log(message) {
    console.log(message)
}