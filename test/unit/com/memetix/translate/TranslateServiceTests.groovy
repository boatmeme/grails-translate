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

import grails.test.*
import com.memetix.mst.language.Language;
import org.apache.log4j.*

class TranslateServiceTests extends GrailsUnitTestCase {
     def translateService
     def log

    protected void setUp() {
        super.setUp()
        setupLogger()
        translateService = new TranslateService()
        def config = new ConfigSlurper().parse(new File( 'grails-app/conf/Config.groovy' ).text) 
        translateService.apiKey = System.properties['test.api.key'] ?: config.translate.test.microsoft.apiKey
    }
    
    protected void tearDown() {
        super.tearDown()
    }
    
    private setupLogger() {
        // build a logger...
        BasicConfigurator.configure() 
        LogManager.rootLogger.level = Level.DEBUG
        log = LogManager.getLogger("TranslateService")

        // use groovy metaClass to put the log into your class
        TranslateService.class.metaClass.getLog << {-> log}
    }
    
    def frenchPhrase = "Il s'agit d'une expression anglais je traduite"
    def englishPhrase = "This is an english phrase I would like translated"
    
    def frTransEnglish = "It is an English expression I translated"

    void testTranslateEnglishToFrench_Specific_Enum() {
        def translation = translateService.translate(englishPhrase, Language.ENGLISH, Language.FRENCH)
        assertEquals frenchPhrase,translation
    }
    
    void testTranslateFrenchToEnglish_Specific_Enum() {
        def translation = translateService.translate(frenchPhrase, Language.FRENCH, Language.ENGLISH)
        assertEquals frTransEnglish,translation
    }
    
    void testTranslateEnglishToFrench_Specific_AutoDetect_Enum() {
        def translation = translateService.translate(englishPhrase, Language.AUTO_DETECT, Language.FRENCH)
        assertEquals frenchPhrase,translation
    }
    
    void testTranslateFrenchToEnglish_Specific_AutoDetect_Enum() {
        def translation = translateService.translate(frenchPhrase, Language.AUTO_DETECT, Language.ENGLISH)
        assertEquals frTransEnglish,translation
    }
    
    void testTranslateEnglishToFrench_AutoDetect_Enum() {
        def translation = translateService.translate(englishPhrase, Language.FRENCH)
        assertEquals frenchPhrase,translation
    }
    
    void testTranslateFrenchToEnglish_AutoDetect_Enum() {
        def translation = translateService.translate(frenchPhrase, Language.ENGLISH)
        assertEquals frTransEnglish,translation
    }
    
    void testTranslateEnglishToFrench_Specific_String_CAPS() {
        def translation = translateService.translate(englishPhrase, "EN", "FR")
        assertEquals frenchPhrase,translation
    }
    
    void testTranslateFrenchToEnglish_Specific_String_CAPS() {
        def translation = translateService.translate(frenchPhrase, "FR", "EN")
        assertEquals frTransEnglish,translation
    }
    
    void testTranslateEnglishToFrench_Specific_AutoDetect_String_CAPS() {
        def translation = translateService.translate(englishPhrase, "", "FR")
        assertEquals frenchPhrase,translation
    }
    
    void testTranslateFrenchToEnglish_Specific_AutoDetect_String_CAPS() {
        def translation = translateService.translate(frenchPhrase, "", "EN")
        assertEquals frTransEnglish,translation
    }
    
    void testTranslateEnglishToFrench_AutoDetect_String_CAPS() {
        def translation = translateService.translate(englishPhrase, "FR")
        assertEquals frenchPhrase,translation
    }
    
    void testTranslateFrenchToEnglish_AutoDetect_String_CAPS() {
        def translation = translateService.translate(frenchPhrase, "EN")
        assertEquals frTransEnglish,translation
    }
    
    void testTranslateEnglishToFrench_Specific_String_LC() {
        def translation = translateService.translate(englishPhrase, "en", "fr")
        assertEquals frenchPhrase,translation
    }
    
    void testTranslateFrenchToEnglish_Specific_String_LC() {
        def translation = translateService.translate(frenchPhrase, "fr", "en")
        assertEquals frTransEnglish,translation
    }
    
    void testTranslateEnglishToFrench_Specific_AutoDetect_String_LC() {
        def translation = translateService.translate(englishPhrase, "", "fr")
        assertEquals frenchPhrase,translation
    }
    
    void testTranslateFrenchToEnglish_Specific_AutoDetect_String_LC() {
        def translation = translateService.translate(frenchPhrase, "", "en")
        assertEquals frTransEnglish,translation
    }
    
    void testTranslateEnglishToFrench_AutoDetect_String_LC() {
        def translation = translateService.translate(englishPhrase, "fr")
        assertEquals frenchPhrase,translation
    }
    
    void testTranslateFrenchToEnglish_AutoDetect_String_LC() {
        def translation = translateService.translate(frenchPhrase, "en")
        assertEquals frTransEnglish,translation
    }
    
    void testDetectEnglish() {
        def detect = translateService.detect(englishPhrase)
        assertEquals Language.ENGLISH,Language.fromString(detect)
    }
    
    void testDetectFrench() {
        def detect = translateService.detect(frenchPhrase)
        assertEquals Language.FRENCH,Language.fromString(detect)
    }
    
    void testLanguageList() {
        def languages = translateService.getLanguages()
        assertEquals 36, languages.size()
        def i = 0
    }
    
    void testLocalizedLanguageList() {
        def languages = translateService.getLanguages(Language.VIETNAMESE)
        assertEquals 36, languages.size()
    }
    
    void testFromLangBad_Exception() {
        def orig = "This is an english phrase I would like translated"
        def message = shouldFail(InvalidLanguageException) {
            translateService.translate(orig, "NO_LANG", Language.FRENCH)
        }
        assertEquals "FROM language is invalid",message
        
        message = shouldFail(TranslationException) {
            translateService.translate(orig, "NO_LANG", Language.FRENCH)
        }
        assertEquals "FROM language is invalid",message
    }
    
    void testToLangBad_Exception() {
        def orig = "This is an english phrase I would like translated"
        def message = shouldFail(InvalidLanguageException) {
            translateService.translate(orig, Language.FRENCH,"NO_LANG")
        }
        assertEquals "TO language is invalid",message
        
        message = shouldFail(TranslationException) {
            translateService.translate(orig, Language.FRENCH,"NO_LANG")
        }
        assertEquals "TO language is invalid",message
    }
    
     void testTranslateToAutoDetect_Exception() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def message = shouldFail(InvalidLanguageException) {
            translateService.translate(orig, Language.FRENCH,Language.AUTO_DETECT)
        }
        assertEquals "Cannot AUTO DETECT the language to Translate TO. Microsoft does not read minds.",message
    }
    
    void testTranslateIncorrectOriginLanguage() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def translation = translateService.translate(orig, "en","en")
        assertEquals "Il s'agit d'une phrase en anglais que je voudrais traduire",translation
    }
    
    void testTranslateGetLanguageName_String() {
        def language = translateService.getLanguageName("en")
        assertEquals "English",language
        language = translateService.getLanguageName("fr")
        assertEquals "French",language
    }
    
    void testTranslateGetLanguageNameLocalized_String() {
        def language = translateService.getLanguageName("en","fr")
        assertEquals "Anglais",language
        language = translateService.getLanguageName("fr","en")
        assertEquals "French",language
    }
    
    void testTranslateGetLanguageNameLocalized_Enum() {
        def language = translateService.getLanguageName(Language.ENGLISH,"fr")
        assertEquals "Anglais",language
        language = translateService.getLanguageName(Language.FRENCH,Language.ENGLISH)
        assertEquals "French",language
    }
    
    void testTranslateGetLanguageNameLocalized_ErrorLocale() {
        def message = shouldFail(InvalidLanguageException) {
            def language = translateService.getLanguageName("en","fro")
        }
        assertEquals "Locale is invalid",message
    }
    
    void testTranslateGetLanguageNameLocalized_ErrorCode() {
        def message = shouldFail(InvalidLanguageException) {
            def language = translateService.getLanguageName("eng","fr")
        }
        assertEquals "Language Code is invalid",message
    }
    
    void testBreakSentences() {
        def intArr = translateService.breakSentences("This is my sentence. That is your sentence. We all have our sentences.", "en")
        assertEquals 3,intArr.length
        assertEquals 21, intArr[0]
        assertEquals 23, intArr[1]
        assertEquals 26, intArr[2]
    }
    
    void testBreakSentences_AUTO_DETECT_ErrorCode() {
        def message = shouldFail(InvalidLanguageException) {
            def intArr = translateService.breakSentences("This is my sentence. That is your sentence. We all have our sentences.", Language.AUTO_DETECT)
        }
        assertEquals "From Language is invalid", message
    }
    
    
}
