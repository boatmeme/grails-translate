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
import com.memetix.mst.Language;
import org.apache.log4j.*

class TranslateServiceTests extends GrailsUnitTestCase {
    def translateService
    def log
    def apiKey = "0B4B2CAA973775DBE72569A29C1A08DA55C88441"
     protected void setUpConfig() {
        def mockConfig = new ConfigObject() 
        mockConfig.translate.microsoft.apiKey=apiKey
        translateService.grailsApplication = new Expando(config: mockConfig)
    }
    
    protected void setUp() {
        super.setUp()
        setupLogger()
        translateService = new TranslateService()
        setUpConfig()
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

    void testTranslateEnglishToFrench_Specific_Enum() {
        def orig = "This is an english phrase I would like translated"
        def translation = translateService.translate(orig, Language.ENGLISH, Language.FRENCH)
        assertEquals "Il s'agit d'une phrase en anglais que je voudrais traduire",translation
    }
    
    void testTranslateFrenchToEnglish_Specific_Enum() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def translation = translateService.translate(orig, Language.FRENCH, Language.ENGLISH)
        assertEquals "This is a sentence in English that I would translate",translation
    }
    
    void testTranslateEnglishToFrench_Specific_AutoDetect_Enum() {
        def orig = "This is an english phrase I would like translated"
        def translation = translateService.translate(orig, Language.AUTO_DETECT, Language.FRENCH)
        assertEquals "Il s'agit d'une phrase en anglais que je voudrais traduire",translation
    }
    
    void testTranslateFrenchToEnglish_Specific_AutoDetect_Enum() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def translation = translateService.translate(orig, Language.AUTO_DETECT, Language.ENGLISH)
        assertEquals "This is a sentence in English that I would translate",translation
    }
    
    void testTranslateEnglishToFrench_AutoDetect_Enum() {
        def orig = "This is an english phrase I would like translated"
        def translation = translateService.translate(orig, Language.FRENCH)
        assertEquals "Il s'agit d'une phrase en anglais que je voudrais traduire",translation
    }
    
    void testTranslateFrenchToEnglish_AutoDetect_Enum() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def translation = translateService.translate(orig, Language.ENGLISH)
        assertEquals "This is a sentence in English that I would translate",translation
    }
    
    void testTranslateEnglishToFrench_Specific_String_CAPS() {
        def orig = "This is an english phrase I would like translated"
        def translation = translateService.translate(orig, "EN", "FR")
        assertEquals "Il s'agit d'une phrase en anglais que je voudrais traduire",translation
    }
    
    void testTranslateFrenchToEnglish_Specific_String_CAPS() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def translation = translateService.translate(orig, "FR", "EN")
        assertEquals "This is a sentence in English that I would translate",translation
    }
    
    void testTranslateEnglishToFrench_Specific_AutoDetect_String_CAPS() {
        def orig = "This is an english phrase I would like translated"
        def translation = translateService.translate(orig, "", "FR")
        assertEquals "Il s'agit d'une phrase en anglais que je voudrais traduire",translation
    }
    
    void testTranslateFrenchToEnglish_Specific_AutoDetect_String_CAPS() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def translation = translateService.translate(orig, "", "EN")
        assertEquals "This is a sentence in English that I would translate",translation
    }
    
    void testTranslateEnglishToFrench_AutoDetect_String_CAPS() {
        def orig = "This is an english phrase I would like translated"
        def translation = translateService.translate(orig, "FR")
        assertEquals "Il s'agit d'une phrase en anglais que je voudrais traduire",translation
    }
    
    void testTranslateFrenchToEnglish_AutoDetect_String_CAPS() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def translation = translateService.translate(orig, "EN")
        assertEquals "This is a sentence in English that I would translate",translation
    }
    
    void testTranslateEnglishToFrench_Specific_String_LC() {
        def orig = "This is an english phrase I would like translated"
        def translation = translateService.translate(orig, "en", "fr")
        assertEquals "Il s'agit d'une phrase en anglais que je voudrais traduire",translation
    }
    
    void testTranslateFrenchToEnglish_Specific_String_LC() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def translation = translateService.translate(orig, "fr", "en")
        assertEquals "This is a sentence in English that I would translate",translation
    }
    
    void testTranslateEnglishToFrench_Specific_AutoDetect_String_LC() {
        def orig = "This is an english phrase I would like translated"
        def translation = translateService.translate(orig, "", "fr")
        assertEquals "Il s'agit d'une phrase en anglais que je voudrais traduire",translation
    }
    
    void testTranslateFrenchToEnglish_Specific_AutoDetect_String_LC() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def translation = translateService.translate(orig, "", "en")
        assertEquals "This is a sentence in English that I would translate",translation
    }
    
    void testTranslateEnglishToFrench_AutoDetect_String_LC() {
        def orig = "This is an english phrase I would like translated"
        def translation = translateService.translate(orig, "fr")
        assertEquals "Il s'agit d'une phrase en anglais que je voudrais traduire",translation
    }
    
    void testTranslateFrenchToEnglish_AutoDetect_String_LC() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def translation = translateService.translate(orig, "en")
        assertEquals "This is a sentence in English that I would translate",translation
    }
    
    void testDetectEnglish() {
        def orig = "This is an english phrase I would like translated"
        def detect = translateService.detect(orig)
        assertEquals Language.ENGLISH,Language.fromString(detect)
    }
    
    void testDetectFrench() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def detect = translateService.detect(orig)
        assertEquals Language.FRENCH,Language.fromString(detect)
    }
    
    void testLanguageList() {
        def languages = translateService.getLanguages()
        assertEquals 92, languages.size()
        def i = 0
        for(lang in Language.values()) {
            assertEquals lang.toString(), languages.get(lang.name())
        }
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
        assertEquals "Cannot AUTO DETECT the language to Translate TO. Google does not yet read minds.",message
    }
    
    void testTranslateIncorrectOriginLanguage() {
        def orig = "Il s'agit d'une phrase en anglais que je voudrais traduire"
        def translation = translateService.translate(orig, "en","en")
        assertEquals "Il s'agit d'une phrase en anglais que je voudrais traduire",translation
    }
    
    void testTranslateGetLanguageName() {
        def language = translateService.getLanguageName("en")
        assertEquals "ENGLISH",language
        language = translateService.getLanguageName("fr")
        assertEquals "FRENCH",language
    }
}
