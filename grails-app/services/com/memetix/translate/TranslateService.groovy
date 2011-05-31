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
package com.memetix.translate;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import com.memetix.mst.detect.Detect;
import com.memetix.translate.LRUCache;
import org.springframework.beans.factory.InitializingBean 

/**
 * TranslateService
 * 
 * Provides a service that wraps the Google Translation API. 
 * 
 * @author Jonathan Griggs  <jonathan.griggs @ gmail.com>
 * @version     1.0   2011.05.24                              
 * @since       1.0   2011.05.24                            
 */

class TranslateService implements InitializingBean {
    def grailsApplication
    static transactional = false
    def languageMap
    def httpReferrer
    def apiKey
    def maxTCacheSize
    def maxDCacheSize
    def tCache
    def dCache
    
    // Configure vars for Grails 1.4.0 compatibility  (ConfigHolder deprecation)
    void afterPropertiesSet() { 
        httpReferrer = grailsApplication?.config?.grails?.serverURL ?: 'http://localhost/translate'
        apiKey = grailsApplication?.config?.translate?.microsoft?.apiKey
        maxTCacheSize = grailsApplication?.config?.translate?.translation?.cache?.maxSize ?: 1000
        maxDCacheSize = grailsApplication?.config?.translate?.detection?.cache?.maxSize ?: 1000
        tCache = new LRUCache(maxTCacheSize)
        dCache = new LRUCache(maxDCacheSize)
    } 

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
     * @version     1.0   2011.05.24                              
     * @since       1.0   2011.05.24   
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
     * @version     1.0   2011.05.24                              
     * @since       1.0   2011.05.24   
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
     * @version     1.0   2011.05.24                              
     * @since       1.0   2011.05.24   
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
        return detectedLanguage?.toString()
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
     * @version     1.0   2011.05.24                              
     * @since       1.0   2011.05.24   
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
     * @return A String representing the full name of the language, null if no match
     *
     * @version     1.0   2011.05.26                              
     * @since       1.0   2011.05.26   
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
