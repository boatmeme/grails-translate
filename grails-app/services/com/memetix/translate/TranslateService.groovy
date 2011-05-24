package com.memetix.translate

import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.google.api.detect.Detect;
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * TranslateService
 * 
 * Provides a service that wraps the Google Translation API. 
 * 
 * @author Jonathan Griggs  <jonathan.griggs @ gmail.com>
 * @version     0.1   2011.05.24                              
 * @since       0.1   2011.05.24                            
 */

class TranslateService {

    static transactional = false
    static languageMap
    static httpReferrer = ConfigurationHolder?.config?.grails?.serverURL ?: 'http://localhost/translate'
    static apiKey = ConfigurationHolder?.config?.translate?.google?.apiKey

    /**
     * translate(originText,fromLang,toLang)                         
     *
     * Takes a String to be translated, the from language, and the to language and calls the Google Translation API
     * Returns the results.
     * 
     * The FROM and TO Language can either be a string, the language abbreviation (ex. "en" or "fr") OR
     * it can be an instance of the Google API package Language Enum
     * 
     * Throws InvalidLanguageExceptions if the from or to language is invalid.
     * 
     * If the user has set an API Key, method will send that, also
     * 
     * @param  originText A String to be translated
     * @param  fromLang A String representing the google abbreviation for a language (ex. "en" or "fr"), OR an instance of the Google API Language Enum            
     * @param  toLang A String representing the google abbreviation for a language (ex. "en" or "fr"), OR an instance of the Google API Language Enum            
     * @return The translated String
     *
     * @version     0.1   2011.05.24                              
     * @since       0.1   2011.05.24   
     */
    
    def translate(originText,fromLang,toLang) {
        def lFrom = Language.fromString(fromLang?.toString()?.toLowerCase())
        if(!lFrom) {
            throw new InvalidLanguageException( 
                message:"FROM language is invalid",
                originalText: originText.toString(),
                fromLanguage:fromLang.toString(),
                toLanguage:toLang.toString())
        }
        
        def lTo = Language.fromString(toLang?.toString()?.toLowerCase())
        if(!lTo) {
            throw new InvalidLanguageException( 
                message:"TO language is invalid",
                originalText: originText.toString(),
                fromLanguage:fromLang.toString(),
                toLanguage:toLang.toString())
        } else if(lTo==Language.AUTO_DETECT) {
            throw new InvalidLanguageException( 
                message:"Cannot AUTO DETECT the language to Translate TO. Google does not yet read minds.",
                originalText: originText.toString(),
                fromLanguage:fromLang.toString(),
                toLanguage:toLang.toString())
        }
            
        // Set the HTTP referrer to your website address.
        Translate.setHttpReferrer(httpReferrer);
        
        // If app has set translate.google.apiKey, then by all means, use it
        if(apiKey)
            Translate.setKey(apiKey)
        //Run the translation
        def translatedText = Translate.execute(originText,lFrom,lTo);
        return translatedText
    }
    
    /**
     * translate(originText, toLang)                         
     *
     * Takes a String to be translated and the TO language and calls the Google Translation API
     * Returns the results.
     * 
     * The TO Language can either be a string, the language abbreviation (ex. "en" or "fr") OR
     * it can be an instance of the Google API package Language Enum
     * 
     * Calls the overloaded translate() with Language.AUTO_DETECT as the FROM language
     * 
     * Throws InvalidLanguageExceptions if the from or to language is invalid.
     * 
     * If the user has set an API Key, method will send that, also
     * 
     * @param  originText A String to be translated
     * @param  toLang A String representing the google abbreviation for a language (ex. "en" or "fr"), OR an instance of the Google API Language Enum            
     * @return The translated String
     *
     * @version     0.1   2011.05.24                              
     * @since       0.1   2011.05.24   
     */
    
    def translate(originText, toLang) {
        return translate(originText,Language.AUTO_DETECT,toLang)
    }
    
    /**
     * detect()                         
     *
     * Takes a String on which to attempt to detect the language
     * 
     * If the user has set an API Key, method will send that, also
     * 
     * @param  originText A String used to detect the language
     * @return A String; Google's best guess at a Language
     *
     * @version     0.1   2011.05.24                              
     * @since       0.1   2011.05.24   
     */
    def detect(originText) {
        // Set the HTTP referrer to your website address.
        Detect.setHttpReferrer(httpReferrer);
        // If app has set translate.google.apiKey, then by all means, use it
        if(apiKey)
            Detect.setKey(apiKey)
            
        def detectedLanguage = Detect.execute(originText)
        return detectedLanguage?.language?.toString()
    }
    
    /**
     * getLanguages()                         
     *
     * Returns a Map with all Language values supported by the Google Translate API
     * 
     * Key = The full name of the Language
     * Value = The abbreviation value that is required by the Google Translate API
     * 
     * The Value is the one that should be used on all TranslateService() method calls
     * 
     * @return A Map with key/value of Language Name / Language Abbreviation. In alphabetical order, by key
     *
     * @version     0.1   2011.05.24                              
     * @since       0.1   2011.05.24   
     */
    def getLanguages() {
        if(!languageMap) {
            languageMap = new TreeMap()
            for(lang in Language?.values()) {
                languageMap.put(lang.name(),lang.language)
            }
        }
        return languageMap
    }
    
}
