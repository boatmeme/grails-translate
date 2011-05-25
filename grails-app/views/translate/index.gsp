<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="main" />
    <style type="text/css" media="screen">
        #container {
            margin-top:20px;
            margin-left:30px;
        }
        textarea {
          width: 350px;
          height: 125px;
          margin: 10px;
        }
        #translation {
          background-color: #FFC2CE;
          border: 4px solid #80B3FF;
          padding: 5px;
          width: 350px;
        }
        #tab {
          margin: 5px;
          background-color: #80B3FF;
          display: inline;
          padding-left: 5px;
          padding-right: 5px;
          color:white;
        }
    </style>
    <title>grails-translate plugin</title>
  </head>
  <body>
<div id="container">
  <g:form method="post">
    Original Text<br/>
    <textarea style="width:300px;height:100px;" name="originalText">${params?.originalText?.trim()}</textarea>
    <br/>
    <translate:languageSelect name='toLang' value="${params?.toLang}"/>
    <g:actionSubmit value="Tranlsate" action="Translate"/>
    <g:actionSubmit value="Detect Language" action="Detect"/>
  </g:form><br/>
    <g:if test="${translation}">
      <div id="tab">Translation</div><div id="translation"> ${translation} </div>
    </g:if>
    <g:if test="${language}">
      <div id="tab">Language</div><div id="translation"><translate:getLanguageName code='${language}'/></div>
    </g:if>
</div>
  </body>
</html>
