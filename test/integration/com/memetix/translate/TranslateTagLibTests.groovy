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
    
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testLanguageSelect_AutoDetectExclude() {
        def template = applyTemplate("<translate:languageSelect excludeAuto='true' name='languages' value='fr'/>")
        assertTrue template?.contains('<select name="languages" >')
        assertTrue template?.contains('<option value="en">ENGLISH</option>')
        assertTrue template?.contains('<option SELECTED value="fr">FRENCH</option>')
        assertFalse template?.contains('<option value="">AUTO_DETECT</option>')
        assertTrue template?.contains('</select>') 
    }
    
    void testLanguageSelect_AutoDetect() {
        def template = applyTemplate("<translate:languageSelect name='languages' value='fr'/>")
        assertTrue template?.contains('<select name="languages" >')
        assertTrue template?.contains('<option value="en">ENGLISH</option>')
        assertTrue template?.contains('<option SELECTED value="fr">FRENCH</option>')
        assertTrue template?.contains('<option value="">AUTO_DETECT</option>')
        assertTrue template?.contains('</select>')
        
    }
    
    void testLanguageSelect_AutoDetect_Default() {
        def template = applyTemplate("<translate:languageSelect name='languages'/>")
        assertTrue template?.contains('<select name="languages" >')
        assertTrue template?.contains('<option value="en">ENGLISH</option>')
        assertTrue template?.contains('<option value="fr">FRENCH</option>')
        assertTrue template?.contains('<option SELECTED value="">AUTO_DETECT</option>')
        assertTrue template?.contains('</select>')    
    }
    
    void testLanguageSelect_AutoDetectExclude_Default() {
        def template = applyTemplate("<translate:languageSelect excludeAuto='true' name='languages'/>")
        assertTrue template?.contains('<select name="languages" >')
        assertTrue template?.contains('<option value="en">ENGLISH</option>')
        assertTrue template?.contains('<option value="fr">FRENCH</option>')
        assertFalse template?.contains('<option value="">AUTO_DETECT</option>')
        assertTrue template?.contains('</select>')
        
    }
    
    void testTranslateTextBodyToOnly() {
        def template = applyTemplate("<translate:translateText toLang='fr'>This is an english phrase I would like translated</translate:translateText>")
        assertOutputEquals('Il s\'agit d\'une phrase en anglais que je voudrais traduire',template)
    }
    
    void testTranslateTextBodyToAndFrom() {
        def template = applyTemplate("<translate:translateText toLang='fr' fromLang='en'>This is an english phrase I would like translated</translate:translateText>")
        assertOutputEquals('Il s\'agit d\'une phrase en anglais que je voudrais traduire',template)
    }
    
    void testDetectLanguageFrench() {
        def template = applyTemplate("<translate:detectLanguage text=\"Il s'agit d'une phrase en anglais que je voudrais traduire\"/>")
        assertOutputEquals('FRENCH',template)
    }
    void testDetectLanguageEnglish() {
        def template = applyTemplate("<translate:detectLanguage text=\"This is an english phrase I would like translated\"/>")
        assertOutputEquals('ENGLISH',template)
    }
    void testGetLanguageName() {
        def template = applyTemplate("<translate:getLanguageName code=\"fr\"/>")
        assertOutputEquals('FRENCH',template)
    }
}
