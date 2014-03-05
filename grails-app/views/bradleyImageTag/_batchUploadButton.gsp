
<uploader:uploader id="${id}" url="${g.createLink([controller: 'bradleyImage', action: 'upload'])}">

    <uploader:onComplete>
        if (responseJSON.success) {
            jQuery('#idCollection').val(jQuery('#idCollection').val() + "," + responseJSON.imageId);
            jQuery('#submitButton').show();
        }
    </uploader:onComplete>

</uploader:uploader>

<g:form controller="bradleyImage" action="batchImageEdit" method="GET">

    <input id="idCollection" type="hidden" name="id" value=""/>

    <g:submitButton name="submit" value="${g.message([code: 'default.bradleyimage.batchupload.gotoimages.button'])}" style="display:none" id="submitButton"/>

</g:form>

<g:javascript>

    jQuery("#idCollection").val("");

</g:javascript>