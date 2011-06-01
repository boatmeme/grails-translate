/**
*
*   Copyright 2011 Jonathan Griggs
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
**/
package com.memetix.translate

import grails.converters.JSON
import grails.converters.XML
import groovy.xml.MarkupBuilder

/**
 * TranslateController provides an example / test implementation of the Translate plugin
 * 
 * /index action displays the form and results
 * 
 * /translate action performs Translate API calls and renders the index view
 * /detect action performs Detect Language API calls and renders the index view
 * 
 * @author Jonathan Griggs  <jonathan.griggs @ gmail.com>
 * @version     1.0   2011.05.26                              
 * @since       1.0   2011.05.24    
 */
class TranslateController {
    def translateService
    def index = { 
    
    }
    
    def translate = {
        flash.clear()
        def translation = params?.originalText
        try {
            translation = translateService.translate(params?.originalText,params?.toLang)
        } catch(Exception e) {
            log.error e
            flash.error = "Translation Error: ${e.message}"
        }
        render(view:'index',model:[translation:translation])
    }
    
    def detect = {
        flash.clear()
        def detected
        try {
            detected = translateService.detect(params?.originalText)
        } catch(Exception e) {
            detected = "UNKNOWN"
            log.error e
            flash.error = "Language Detection Error: ${e.message}"
        }
        render(view:'index',model:[language:detected])
    }
    
    def ajax = {
        def translationResult = params?.originalText
        def toLang = params?.toLang
        def fromLang = params?.fromLang
        
        def startTime = System.currentTimeMillis()
        def responseObj
        
        def format = params?.format?.toLowerCase() ?: 'json'
        
        if(!translationResult) {
            responseObj = ["status_code":"500","status_text":"MISSING_PARAMETER","errors":["Please provide an 'originalText' parameter with the text to be translated"],"data":[]]
        } else if(!toLang) {
            responseObj = ["status_code":"500","status_text":"MISSING_PARAMETER","errors":["Please provide a 'toLang' parameter with the target language ISO code"],"data":[]]
        } else {
            def expandedMap
            responseObj = ["status_code":"200","status_text":"OK","data":[],"errors":[]]
            try {
                if(fromLang)
                    translationResult = translateService.translate(params?.originalText,fromLang,toLang)
                else
                    translationResult = translateService.translate(params?.originalText,toLang)
                responseObj."data" = ["translation":translationResult]
            } catch(Exception e) {
                log.error e
                responseObj."status_code" = "500"
                responseObj."status_text" = "INVALID_LANGUAGE_PAIR"
                responseObj."errors".add(e?.getMessage())
            }
        }
        
        if(format=='json') {
            responseObj."elapsedTime" = System.currentTimeMillis()-startTime
            render responseObj as JSON
        } else if(format=='xml'){
            def b = new groovy.xml.StreamingMarkupBuilder()
            b.encoding = "UTF-8"
            def xml = b.bind {
                    mkp.xmlDeclaration()
                    response {
                        status_code(responseObj."status_code")
                        status_text(responseObj."status_text")
                        errors {
                            for(e in responseObj."errors") {
                                error(e)
                            }
                        }
                        data {
                            if(responseObj?.data?.translation)
                                translation(responseObj."data"."translation")
                        }
                        elapsedTime(System.currentTimeMillis()-startTime)
                    }
            }
            render(text: xml.toString(),contentType:'text/xml') 
        } else {
            render responseObj."data".translation
        }
    }
    
    def detectAjax = {
        def languageResult = params?.originalText
        def startTime = System.currentTimeMillis()
        def responseObj
        
        def format = params?.format?.toLowerCase() ?: 'json'
        
        if(!languageResult) {
            responseObj = ["status_code":"500","status_text":"MISSING_PARAMETER","errors":["Please provide an 'originalText' parameter with the text to be translated"],"data":[]]
        } else {
            def expandedMap
            responseObj = ["status_code":"200","status_text":"OK","data":[],"errors":[]]
            try {
                languageResult = translateService.detect(params?.originalText)
                responseObj."data" = ["code":languageResult,"language":translateService.getLanguageName(languageResult)]
            } catch(Exception e) {
                log.error e
                responseObj."status_code" = "500"
                responseObj."status_text" = "INVALID_LANGUAGE_PAIR"
                responseObj."errors".add(e?.getMessage())
            }
        }
        
        if(format=='json') {
            responseObj."elapsedTime" = System.currentTimeMillis()-startTime
            render responseObj as JSON
        } else if(format=='xml'){
            def b = new groovy.xml.StreamingMarkupBuilder()
            b.encoding = "UTF-8"
            def xml = b.bind {
                    mkp.xmlDeclaration()
                    response {
                        status_code(responseObj."status_code")
                        status_text(responseObj."status_text")
                        errors {
                            for(e in responseObj."errors") {
                                error(e)
                            }
                        }
                        data {
                            if(responseObj?.data?.language) {
                                language {
                                    name(responseObj."data"."language")
                                    code(responseObj."data"."code")
                                }
                            }
                        }
                        elapsedTime(System.currentTimeMillis()-startTime)
                    }
            }
            render(text: xml.toString(),contentType:'text/xml') 
        } else {
            render responseObj."data".language
        }
    }
}
