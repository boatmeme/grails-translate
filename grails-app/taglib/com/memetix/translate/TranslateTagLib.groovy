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
/**
 * TranslateTagLib 
 * 
 * namespace:translate
 * 
 * tags: translateText
 *       languageSelect
 *       detectLanguage
 *       getLanguageName
 * 
 * @author Jonathan Griggs  <jonathan.griggs @ gmail.com>
 * @version     1.0     2011.05.26                              
 * @since       1.0     2011.05.24                            
 */
class TranslateTagLib {
    static namespace = "translate"
    def translateService
    
    def languageSelect = { attrs, body ->
        def select = out
        def locale = attrs.remove('locale') ?: "en"
        def value = attrs.remove('value') ?: ""
        def excludeAuto = attrs.remove('excludeAuto') ?: false;
        def languages = translateService.getLanguages(locale)
        select << "<select name=\"${attrs.remove('name')?.encodeAsHTML()}\" "
        select << ">"
        def selected = ""
        for(langName in languages.keySet()) {
            if(languages.get(langName) != "" || !excludeAuto) {
                selected = ""
                if(languages.get(langName).equals(value))
                    selected = "SELECTED "
                select << "<option ${selected}value=\"${languages.get(langName)}\">${langName}</option>"
            }
        }
        select << "</select>"
    }
    
     def translateText = { attrs, body ->
        def toLang = attrs?.toLang
        def fromLang = attrs?.fromLang ?: ""
        def translation = translateService.translate(body(),fromLang, toLang)
        out << translation
    }
    
    def detectLanguage = { attrs ->
        def locale = attrs?.locale ?: "en"
        System.out.println ("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!${locale}")
        def text = attrs?.text
        def detect = translateService.detect(text)
        
        def languages = translateService.getLanguages(locale)
        for(lang in languages) {
            println lang.key
            if(lang.value.equals(detect)) {
                detect = lang.key
                break;
            }
        }
        out << detect
    }
    
    def getLanguageName = { attrs ->
        def code = attrs?.code
        def locale = attrs.remove('locale') ?: "en"
        def name = translateService.getLanguageName(code,locale)
        out << name
    }
}
