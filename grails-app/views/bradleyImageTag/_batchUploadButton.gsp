
<uploader:uploader id="${id}" url="${g.createLink([controller: 'bradleyImage', action: 'upload'])}">

    <uploader:onComplete>
        if (responseJSON.success) {
            jQuery('#idCollection').val(jQuery('#idCollection').val() + "," + responseJSON.imageId);
            jQuery('#submitButton').show();
        }
    </uploader:onComplete>

</uploader:uploader>

<g:form controller="image" action="edit" method="GET">

    <input id="idCollection" type="hidden" name="id" value=""/>

    <g:submitButton name="submit" value="Go to images" style="display:none" id="submitButton"/>

</g:form>