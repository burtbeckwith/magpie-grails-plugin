

var errands = new Array()

function prepareConsole() {
    $('#errandsDisplay').hide()
    requestErrands()

    $('#createNewErrandSubmitButton').click(function(){requestNewErrand(); return false;})
}

function requestNewErrand(){
    log('requestNewErrand')

    var proposedName                            = $('#proposedErrandName').val()
    var proposedUrl                             = $('#proposedErrandUrl').val()
    var proposedCronExpression                  = $('#proposedErrandCronExpression').val()
    var proposedEnforcedContentTypeForRendering = $('#proposedErrandEnforcedContentTypeForRendering').val()

    log("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
    log("proposedName:" + proposedName)
    log("proposedUrl:" + proposedUrl)
    log("proposedCronExpression:" + proposedCronExpression)
    log("proposedEnforcedContentTypeForRendering:" + proposedEnforcedContentTypeForRendering)
    log("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")


    $.ajax({
        method:'POST',
        url: '/magpie/restfulMagpie/errands',
        data: {name:proposedName, url:proposedUrl,cronExpression:proposedCronExpression,enforcedContentTypeForRendering:proposedEnforcedContentTypeForRendering},
        statusCode:
        {
            201: onHavingSuccessfullyCreatedANewErrand,
            400: onHavingFailedToCreateANewErrandDueToValidationErrors
            //400: function(){onHavingFailedToCreateANewErrand();}
        },
        // error: onHavingFailedToCreateANewErrand,
        dataType: 'json'
    });

}


function onHavingSuccessfullyCreatedANewErrand(){
    log('onHavingSuccessfullyCreatedANewErrand')

    $('#proposedErrandFeedback').html('Your new Errand has been successfully created.')

    requestErrands()
}

function onHavingFailedToCreateANewErrandDueToValidationErrors(xmlHttpRequest){

    log("rawProposedErrand:" + xmlHttpRequest.responseText)

    var proposedErrand = $.parseJSON(xmlHttpRequest.responseText)

    var $proposedErrandFeedback                 = $('#proposedErrandFeedback')
    var $proposedErrandNameFeedback             = $('#proposedErrandNameFeedback')
    var $proposedErrandUrlFeedback              = $('#proposedErrandUrlFeedback')
    var $proposedErrandCronExpressionFeedback   = $('#proposedErrandCronExpressionFeedback')

    if(proposedErrand.fieldErrors){
        $proposedErrandFeedback.html('There are problems with your proposed Errand; please correct them and re-submit.')
    }

    $proposedErrandNameFeedback.html('')
    $proposedErrandUrlFeedback.html('')
    $proposedErrandCronExpressionFeedback.html('')

    for(x=0;x<proposedErrand.fieldErrors.length;x++){

        var fieldError = proposedErrand.fieldErrors[x]
        var field           = fieldError.field
        var rejectedValue   = fieldError.rejectedValue
        var reason          = fieldError.code

        log("Field Error (" + field + ") Rejected Value (" + rejectedValue + ") Reason (" + reason + ")")

        if(field=='name') {

            var errorMessageForName
            if(reason=='unique'){
                errorMessageForName = 'An Errand already exists by this name; check below and please ensure a unique name.'
            }
            else if(reason=='blank'){
                errorMessageForName = 'Please provide a name for your new Errand; please note that it must be unique.'
            }

            if(!errorMessageForName) errorMessageForName = reason

            $proposedErrandNameFeedback.append(errorMessageForName)

        }
        else if(field=='url') {

            var errorMessageForUrl
            if(reason=='nullable'){
                errorMessageForUrl = 'Please provide a URL that this Errand will Fetch.'
            }

            if(!errorMessageForUrl) errorMessageForUrl = reason

            $proposedErrandUrlFeedback.append(errorMessageForUrl)
        }
        else if(field=='cronExpression') {

            var errorMessageForCronExpression
            if(reason=='blank'){
                errorMessageForCronExpression = 'Please provide a Cron expression, to schedule the Fetch.'
            }

            if(!errorMessageForCronExpression) errorMessageForCronExpression = reason

            $proposedErrandCronExpressionFeedback.append(errorMessageForCronExpression)
        }


    }

    log('onHavingFailedToCreateANewErrand:' + proposedErrand.fieldErrors)
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

    var $errandRows = $('#errandRows')

    $errandRows.html('')

    for(var x=0; x < errands.length;x++){
        var errand = errands[x]

        var errandRow = ''

        var errandHeaderRow = ''

        errandHeaderRow += "<tr bgcolor='lightyellow'>"
        errandHeaderRow += "<td width='30%'>Name</td>"
        errandHeaderRow += "<td width='10%'>Url</td>"
        errandHeaderRow += "<td width='20%'>Cron</td>"
        errandHeaderRow += "<td width='20%'>Enforced Content Type</td>"
        errandHeaderRow += "<td width='10%'>Active</td>"
        errandHeaderRow += "</tr>"

        errandRow += errandHeaderRow

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


        $errandRows.append(errandRow)

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


        var urlForContents = '/magpie/restfulMagpie/fetches/' + fetchId + '/contents'
        var targetWindow = '_fetchContentsForFetch' + fetchId
        var contentTypeDisplay = (contentSize> 0) ? "<a target='" + targetWindow + "' href='" + urlForContents + "'>" + contentType + "</a>" : contentType

        errandFetchesDisplay += "<tr class='fetchDataRow'>"
        errandFetchesDisplay += '<td>' + date + '</td>'
        errandFetchesDisplay += '<td>' + httpStatusCode + '</td>'
        errandFetchesDisplay += '<td>' + contentTypeDisplay + '</td>'
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