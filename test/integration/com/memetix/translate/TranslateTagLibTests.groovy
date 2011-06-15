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

class TranslateTagLibTests extends GroovyPagesTestCase  {
    def translateService
    def grailsApplication
    
    protected void setUp() {
        super.setUp()
        translateService = new TranslateService()
        translateService.apiKey = System.properties['test.api.key'] ?: grailsApplication.config.translate.test.microsoft.apiKey 
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testLanguageSelect_AutoDetectExclude() {
        def template = applyTemplate("<translate:languageSelect excludeAuto='true' name='languages' value='fr'/>")
        assertTrue template?.contains('<select name="languages" >')
        assertTrue template?.contains('<option value="en">English</option>')
        assertTrue template?.contains('<option SELECTED value="fr">French</option>')
        assertFalse template?.contains('<option value="">Auto Detect</option>')
        assertTrue template?.contains('</select>') 
    }
    
    void testLanguageSelect_AutoDetect() {
        def template = applyTemplate("<translate:languageSelect name='languages' value='fr'/>")
        assertTrue template?.contains('<select name="languages" >')
        assertTrue template?.contains('<option value="en">English</option>')
        assertTrue template?.contains('<option SELECTED value="fr">French</option>')
        assertTrue template?.contains('<option value="">Auto Detect</option>')
        assertTrue template?.contains('</select>')
        
    }
    
    void testLanguageSelect_Localized() {
        def template = applyTemplate("<translate:languageSelect name='languages' locale='fr' value='fr'/>")
        assertTrue template?.contains('<select name="languages" >')
        assertTrue template?.contains('<option value="en">Anglais</option>')
        assertTrue template?.contains('<option SELECTED value="fr">Français</option>')
        assertTrue template?.contains('<option value="">Auto Detect</option>')
        assertTrue template?.contains('</select>')
        
    }
    
    void testLanguageSelect_AutoDetect_Default() {
        def template = applyTemplate("<translate:languageSelect locale='fr' name='languages'/>")
        assertTrue template?.contains('<select name="languages" >')
        assertTrue template?.contains('<option value="en">Anglais</option>')
        assertTrue template?.contains('<option value="fr">Français</option>')
        assertTrue template?.contains('<option SELECTED value="">Auto Detect</option>')
        assertTrue template?.contains('</select>')    
    }
    
    void testLanguageSelect_AutoDetectExclude_Default() {
        def template = applyTemplate("<translate:languageSelect excludeAuto='true' name='languages'/>")
        assertTrue template?.contains('<select name="languages" >')
        assertTrue template?.contains('<option value="en">English</option>')
        assertTrue template?.contains('<option value="fr">French</option>')
        assertFalse template?.contains('<option value="">Auto Detect</option>')
        assertTrue template?.contains('</select>')
        
    }
    
    void testTranslateTextBodyToOnly() {
        def template = applyTemplate("<translate:translateText toLang='fr'>This is an english phrase I would like translated</translate:translateText>")
        assertOutputEquals('Il s\'agit d\'une expression anglais je traduite',template)
    }
    
    void testTranslateTextBodyToAndFrom() {
        def template = applyTemplate("<translate:translateText toLang='fr' fromLang='en'>This is an english phrase I would like translated</translate:translateText>")
        assertOutputEquals('Il s\'agit d\'une expression anglais je traduite',template)
    }
    
    void testDetectLanguageFrench() {
        def template = applyTemplate("<translate:detectLanguage text=\"Il s'agit d'une phrase en anglais que je voudrais traduire\"/>")
        assertOutputEquals('French',template)
    }
    void testDetectLanguageEnglish() {
        def template = applyTemplate("<translate:detectLanguage text=\"This is an english phrase I would like translated\"/>")
        assertOutputEquals('English',template)
    }
    void testDetectLanguageEnglish_Localized() {
        def template = applyTemplate("<translate:detectLanguage locale=\"fr\" text=\"This is an english phrase I would like translated\"/>")
        assertOutputEquals('Anglais',template)
    }
    void testGetLanguageName() {
        def template = applyTemplate("<translate:getLanguageName code=\"fr\"/>")
        assertOutputEquals('French',template)
    }
    void testGetLanguageName_Localized() {
        def template = applyTemplate("<translate:getLanguageName code=\"fr\" locale=\"fr\"/>")
        assertOutputEquals('Français',template)
    }
}
