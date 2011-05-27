package com.memetix.translate;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.google.api.detect.Detect;
import com.memetix.translate.LRUCache;

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
    def grailsApplication
    static transactional = false
    def languageMap
    def httpReferrer = grailsApplication?.config?.grails?.serverURL ?: 'http://localhost/translate'
    def apiKey = grailsApplication?.config?.translate?.google?.apiKey
    def maxTCacheSize = grailsApplication?.config?.translate?.translation?.cache?.maxSize ?: 1000
    def maxDCacheSize = grailsApplication?.config?.translate?.detection?.cache?.maxSize ?: 1000
    def tCache = new LRUCache(maxTCacheSize)
    def dCache = new LRUCache(maxDCacheSize)

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
        log.debug("Executing TranslationService.translate(${originText},${fromLang},${toLang})")
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
        def translatedText
        // If the cache has been configured, try to fetch from it
        if(maxTCacheSize>=0) {
            log.debug("Fetching Translation from Cache")
            translatedText = tCache.get(toLang.toString()+originText.toString())
            //if it is in the cache, then send it on back
            if(translatedText) {
                log.debug("Returning Cached Translation")
                return translatedText
            }
        }
        
        // Set the HTTP referrer to your website address.
        Translate.setHttpReferrer(httpReferrer);
        
        // If app has set translate.google.apiKey, then by all means, use it
        if(apiKey)
            Translate.setKey(apiKey)
        //Run the translation
        translatedText = Translate.execute(originText,lFrom,lTo);
        
        // If the cache has been configured, put into it
        if(maxTCacheSize>=0&&translatedText) {
            log.debug("Caching Translation")
            tCache.put(toLang.toString()+originText.toString(),translatedText)
        }
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
        log.debug("Executing TranslationService.translate(${originText},${toLang})")
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
        log.debug("Executing TranslationService.detect(${originText}")
        def detectedLanguage
        
        //If caching, then try to fetch from the Detect cache
        if(maxDCacheSize>=0) {
            log.debug("Fetching LanguageDetect from Cache")
            detectedLanguage = dCache.get(originText.toString())
            //if it is in the cache, then send it on back
            if(detectedLanguage) {
                log.debug("Returning Cached LanguageDetect")
                return detectedLanguage.language?.toString()
            }
        }
        // Set the HTTP referrer to your website address.
        Detect.setHttpReferrer(httpReferrer);
        // If app has set translate.google.apiKey, then by all means, use it
        if(apiKey)
            Detect.setKey(apiKey)
            
        detectedLanguage = Detect.execute(originText)
        
        //If caching, then store in the Detect cache
        if(maxDCacheSize>=0) {
            log.debug("Caching Language Detect")
            dCache.put(originText.toString(),detectedLanguage)
        }
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
        log.debug("Executing TranslationService.getLanguages()")
        if(!languageMap) {
            log.debug("Initializing getLanguages() language map")
            languageMap = new TreeMap()
            for(lang in Language?.values()) {
                languageMap.put(lang.name(),lang.language)
            }
        }
        return languageMap
    }
    
    /**
     * getLanguageName(code)                         
     *
     * Returns the full name of the language corresponding to the language code passed in
     * 
     * The code is a two-letter ISO Language Code supported by the Google Translation API
     * 
     * 
     * @return A String representing the full name of the language, null if no match
     *
     * @version     1.0   2011.05.24                              
     * @since       1.0   2011.05.24   
     */
    def getLanguageName(code) {
        log.debug("Executing TranslationService.getLanguageName(${code})")
        def languages = getLanguages()
        def name
        for(lang in languages) {
            if(lang.value.equals(code)) {
                name = lang.key
                break;
            }
        }
        return name
    }
    
}
