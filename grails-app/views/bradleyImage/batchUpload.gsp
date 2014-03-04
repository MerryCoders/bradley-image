<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="mainLarge"/>
        <g:set var="entityName" value="${message(code: 'image.label', default: 'Image')}"/>
        <title><g:message code="default.edit.label" args="[entityName]"/></title>
        <r:require module="fileuploader"/>
        <r:layoutResources/>

    </head>

    <body>

        <uploader:uploader id="2" url="${g.createLink([controller: 'bradleyImage', action: 'upload'])}">
            <uploader:onComplete>
                if (responseJSON.success) {
                    $('#idCollection').val($('#idCollection').val() + "," + responseJSON.imageId);
                    $('#submitButton').show();
                }
            </uploader:onComplete>
        </uploader:uploader>

        <g:form controller="image" action="edit" method="GET">

            <input id="idCollection" type="hidden" name="id" value=""/>
            <g:hiddenField name="redirectUrl" value="${redirectUrl}"/>
            <g:hiddenField name="pageInstance.id" value="${pageInstance?.id}"/>
            <g:hiddenField name="productInstance.id" value="${productInstance?.id}"/>
            <g:if test="${imageType}">
                <g:hiddenField name="imageType" value="${imageType}"/>
            </g:if>

            <g:submitButton name="submit" value="Go to images" style="display:none" id="submitButton"/>

        </g:form>

        <r:layoutResources/>

    </body>
</html>