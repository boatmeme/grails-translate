<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="main" />
    <style type="text/css" media="screen">
        #container {
            margin-top:20px;
            margin-left:30px;
            padding:10px;
            background-color: #FBFCD0;
            width:600px;
            border:5px solid #EECD7E;
        }
        #title {
          color: #666666;
          font-family: 'Century Gothic', sans-serif;
          font-size:2em;
          margin:10px;
        }
        textarea {
          width: 90%;
          height: 125px;
          margin: 10px;
        }
        #translation {
          background-color: #FFF3A1;
          border: 4px solid #EECD7E;
          padding: 5px;
        }
        #tab {
          margin: 5px;
          background-color: #EECD7E;
          display: inline;
          padding-left: 5px;
          padding-right: 5px;
          color:#666333;
        }
    </style>
    <title>grails-translate plugin</title>
  </head>
  <body>
    <g:set var="pluginManager" value="${applicationContext.getBean('pluginManager')}"></g:set>
<div id="container">
  <div id="title">grails-translate v<a href="https://github.com/boatmeme/grails-translate/tree/v${pluginManager?.getGrailsPlugin('translate')?.version}">${pluginManager?.getGrailsPlugin('translate')?.version}</a></div>
  <g:form method="post">
    Original Text<br/>
    <textarea name="originalText">${params?.originalText?.trim()}</textarea>
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
