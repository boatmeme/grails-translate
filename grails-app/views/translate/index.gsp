<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>grails-translate plugin</title>
  </head>
  <body>
  <g:form method="post">
    Original Text<br/>
    <textarea style="width:300px;height:100px;" name="originalText">${params?.originalText?.trim()}</textarea>
    <br/>
    <translate:languageSelect name='toLang' value="${params?.toLang}"/>
    <g:actionSubmit value="Tranlsate" action="Translate"/>
    <g:actionSubmit value="Detect Language" action="Detect"/>
  </g:form> 
    <g:if test="${translation}">
      Translation: ${translation}
    </g:if>
    <g:if test="${language}">
      Language: <translate:getLanguageName code='${language}'/>
    </g:if>
  </body>
</html>
