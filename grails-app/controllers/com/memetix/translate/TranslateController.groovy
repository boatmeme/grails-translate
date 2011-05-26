package com.memetix.translate
/**
 * TranslateController provides an example / test implementation of the Translate plugin
 * 
 * /index action displays the form and results
 * 
 * /translate action performs Translate API calls and renders the index view
 * /detect action performs Detect Language API calls and renders the index view
 * 
 * 
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
            flash.error "Language Detection Error: ${e.message}"
        }
        render(view:'index',model:[language:detected])
    }
}
