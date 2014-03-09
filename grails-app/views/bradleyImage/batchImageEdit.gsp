<%@ page contentType="text/html;charset=UTF-8" %>
<html>

    <head>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="mainLarge"/>
        <g:set var="entityName" value="${message(code: 'image.label', default: 'Image')}"/>
        <title><g:message code="default.edit.label" args="[entityName]"/></title>
        <r:require module="batchUploadButton"/>
        <r:layoutResources/>

    </head>

    <body>

        <ul>

            <g:each in="${bradleyImageInstanceList}" var="bradleyImageInstance">

            %{--Edit name, alt-text, and caption--}%
                <li>${bradleyImageInstance} - <bradleyImage:bradleyImg bradleyImage="${bradleyImageInstance}" width="100" height="100" resize="f"/></li>

            </g:each>

        </ul>

        <r:layoutResources/>

    </body>

</html>