package com.memetix.translate

import grails.test.*
import org.apache.log4j.*
import grails.converters.JSON
import grails.converters.XML

class TranslateControllerTests extends ControllerUnitTestCase {
    def log
    def translateService
    
    def englishPhrase = "One day last year, while working on a biography of the publisher Scofield Thayer, I opened a folder of papers related to his magazine The Dial. The folder contained undated letters from the poet E.E. Cummings to Thayer";
    def frenchPhrase = "Un jour de l'an dernier, alors qu'il travaillait sur une biographie de l'éditeur Scofield Thayer, j'ai ouvert un dossier de documents liés à son cadran le magazine. Le dossier contenait des lettres non datées du poète E.E. Cummings à Thayer"
    //def frenchPhrase = "Un jour l'année dernière, tout en travaillant sur une biographie de l'éditeur Scofield Thayer, j'ai ouvert un dossier de documents liés à sa revue The Dial. Le dossier contenait des lettres non datée du poète EE Cummings à Thayer"
    
    def statusCodeOk = "200"
    def statusCodeServerError = "500"
    
    def statusTextOk = "OK"
    def statusTextMissingParameter = "MISSING_PARAMETER"
    def statusTextInvalidPair = "INVALID_LANGUAGE_PAIR"

    def jsonFormat = "json"
    def xmlFormat = "xml"
    
    def missingParameterError = "Please provide an 'originalText' parameter with the text to be translated"
    def translateError = "TO language is invalid"
    def toLangError = "Please provide a 'toLang' parameter with the target language ISO code"
    protected void setUp() {
        super.setUp()
        setupLogger()
        translateService = new TranslateService()
        def config = new ConfigSlurper().parse(new File( 'grails-app/conf/Config.groovy' ).text) 
        translateService.apiKey = System.properties['test.api.key'] ?: config.translate.test.microsoft.apiKey
        controller = new TranslateController()
        controller.translateService = translateService
        controller.response.setCharacterEncoding("UTF-8") 
    }
    
    private setupLogger() {
        // build a logger...
        BasicConfigurator.configure() 
        LogManager.rootLogger.level = Level.DEBUG
        log = LogManager.getLogger("TranslateService")

        // use groovy metaClass to put the log into your class
        TranslateService.class.metaClass.getLog << {-> log}
    }

    protected void tearDown() {
        super.tearDown()
    }
    
    private assertCommonResponseParams(response) {
        assertNotNull   response
        assertNotNull   response.status_code
        assertNotNull   response.status_text
        assertNotNull   response.data
        assertNotNull   response.errors
        assertNotNull   response.elapsedTime
    }
    
    private parseXMLToMap(xmlResponse) {
        def map = new HashMap()
        for(int i=0;i<xmlResponse?.data?.entry?.size();i++) {
            def xmlObj = xmlResponse?.data?.entry[i];
            map.put(xmlObj.translation.toString(), xmlObj) 
        }
        return map
    }

    void testIndex() {
        def model = controller.index()
        assertNull   model
    }
    
    void testTranslate() {
        controller.params.originalText = englishPhrase
        controller.params.toLang = "fr"
        def model = controller.translate()
        assertNotNull   model
        assertNotNull   model.translation
        assertNull      controller.flash.error
        assertEquals    frenchPhrase,model.translation
    }
    
    void testDetect() {
        controller.params.originalText = englishPhrase
        def model = controller.detect()
        assertNotNull   model
        assertNotNull   model.language
        assertNull      controller.flash.error
        assertEquals    "en",model.language
    }
    
    void testTranslate_InvalidLanguagePair() {
        controller.params.originalText = englishPhrase
        controller.params.toLang = "hy"
        def model = controller.translate()
        assertNotNull   model
        assertNotNull   model.translation
        assertNotNull   controller.flash.error
        assertEquals    englishPhrase,model.translation
    }
    
    void testAjax_JSON_Detect() {
        controller.params.originalText = englishPhrase
        controller.detectAjax()    
        
        def jsonResponse = JSON.parse(controller.response.contentAsString)
        assertCommonResponseParams(jsonResponse)
        
        assertEquals statusCodeOk,  jsonResponse.status_code.toString()
        assertEquals statusTextOk,  jsonResponse.status_text.toString()
        assertEquals 2,             jsonResponse.data.size()
        
        assertEquals "en",          jsonResponse?.data?.code         ?.toString()
        assertEquals "English",     jsonResponse?.data?.language     ?.toString()
    }
    
    void testAjax_JSON_Detect_MissingParameter() {
        controller.params.toLang = "hy"
        controller.detectAjax()    
        
        def response = JSON.parse(controller.response.contentAsString)
        assertCommonResponseParams(response)
        
        assertEquals statusCodeServerError,     response    ?.status_code   ?.toString()
        assertEquals statusTextMissingParameter,response    ?.status_text   ?.toString()
        
        assertEquals 1,                         response    ?.errors      ?.size()
        assertEquals missingParameterError,     response    ?.errors[0]  ?.toString()
        assertEquals 0,                         response    ?.data.entry        ?.size()
    }
    
    void testAjax_JSON_Detect_MissingParameter_toLang() {
        controller.params.originalText = "Translation"
        controller.ajax()    
        
        def response = JSON.parse(controller.response.contentAsString)
        assertCommonResponseParams(response)
        
        assertEquals statusCodeServerError,     response    ?.status_code   ?.toString()
        assertEquals statusTextMissingParameter,response    ?.status_text   ?.toString()
        
        assertEquals 1,                         response    ?.errors      ?.size()
        assertEquals toLangError,     response    ?.errors[0]  ?.toString()
        assertEquals 0,                         response    ?.data.entry        ?.size()
    }

    void testAjax_JSON_Translate() {
        controller.params.originalText = englishPhrase
        controller.params.toLang = "fr"
        controller.ajax()    
        
        def jsonResponse = JSON.parse(controller.response.contentAsString)
        assertCommonResponseParams(jsonResponse)
        
        assertEquals statusCodeOk,  jsonResponse.status_code.toString()
        assertEquals statusTextOk,  jsonResponse.status_text.toString()
        assertEquals 1,             jsonResponse.data.size()
        
        assertEquals frenchPhrase,    jsonResponse?.data?.translation     ?.toString()
    }
    
    
    void testAjax_JSON_Translate_InvalidLanguagePair() {
        controller.params.originalText = englishPhrase
        controller.params.toLang = "hy"
        controller.ajax()    
        
        def response = JSON.parse(controller.response.contentAsString)
        assertCommonResponseParams(response)
        
        assertEquals statusCodeServerError,     response    ?.status_code   ?.toString()
        assertEquals statusTextInvalidPair,     response    ?.status_text   ?.toString()
        
        assertEquals 1,                         response    ?.errors     ?.size()
        assertEquals translateError,     response  ?.errors[0]  ?.toString()
        assertEquals 0,                         response    ?.data.entry        ?.size()
    }
    
     void testAjax_JSON_Translate_MissingParameter() {
        controller.params.toLang = "hy"
        controller.ajax()    
        
        def response = JSON.parse(controller.response.contentAsString)
        assertCommonResponseParams(response)
        
        assertEquals statusCodeServerError,     response    ?.status_code   ?.toString()
        assertEquals statusTextMissingParameter,response    ?.status_text   ?.toString()
        
        assertEquals 1,                         response    ?.errors      ?.size()
        assertEquals missingParameterError,     response    ?.errors[0]  ?.toString()
        assertEquals 0,                         response    ?.data.entry        ?.size()
    }
    
    void testAjax_XML_Detect() {
        controller.params.originalText = englishPhrase
        controller.params.format = xmlFormat
        controller.detectAjax()    
        
        def response = XML.parse(controller.response.contentAsString)
        assertCommonResponseParams(response)
        
        assertEquals statusCodeOk,  response.status_code.toString()
        assertEquals statusTextOk,  response.status_text.toString()
        assertEquals 1,             response.data.size()
         
        assertEquals "en",      response?.data?.language?.code     ?.toString()
        assertEquals "English", response?.data?.language?.name     ?.toString()
        
    }
    
    void testAjax_XML_Translate() {
        controller.params.originalText = englishPhrase
        controller.params.toLang = "fr"
        controller.params.format = xmlFormat
        controller.ajax()    
        
        def response = XML.parse(controller.response.contentAsString)
        assertCommonResponseParams(response)
        
        assertEquals statusCodeOk,  response.status_code.toString()
        assertEquals statusTextOk,  response.status_text.toString()
        assertEquals 1,             response.data.size()
         
        assertEquals frenchPhrase,  response?.data?.translation     ?.toString()
    }
    
    void testAjax_XML_Translate_InvalidLanguagePair() {
        controller.params.originalText = englishPhrase
        controller.params.toLang = "hy"
        controller.params.format = xmlFormat
        controller.ajax()    
        
        def response = XML.parse(controller.response.contentAsString)
        assertCommonResponseParams(response)
        
        assertEquals statusCodeServerError,     response    ?.status_code   ?.toString()
        assertEquals statusTextInvalidPair,     response    ?.status_text   ?.toString()
        
        assertEquals 1,                         response    ?.errors     ?.size()
        assertEquals translateError,            response    ?.errors[0]  ?.toString()
        assertEquals 0,                         response    ?.data.entry        ?.size()
    }
    
     void testAjax_XML_Translate_MissingParameter() {
        controller.params.toLang = "hy"
        controller.params.format = xmlFormat
        controller.ajax()

        def response = XML.parse(controller.response.contentAsString)
        assertCommonResponseParams(response)
        
        assertEquals statusCodeServerError,     response    ?.status_code   ?.toString()
        assertEquals statusTextMissingParameter,response    ?.status_text   ?.toString()
        
        assertEquals 1,                         response    ?.errors      ?.size()
        assertEquals missingParameterError,     response    ?.errors[0]  ?.toString()
        assertEquals 0,                         response    ?.data.entry        ?.size()
    }
}
