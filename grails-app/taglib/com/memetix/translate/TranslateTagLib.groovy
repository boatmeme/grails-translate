package com.memetix.translate
/**
 * TranslateTagLib 
 * 
 * namespace:translate
 * 
 * tags: translateText
 *       languageSelect
 *        
 * 
 * @author Jonathan Griggs  <jonathan.griggs @ gmail.com>
 * @version     0.1     2011.05.24                              
 * @since       0.1     2011.05.24                            
 */
class TranslateTagLib {
    static namespace = "translate"
    def translateService
    
    def languageSelect = { attrs, body ->
        def select = out
        def value = attrs.remove('value') ?: ""
        def excludeAuto = attrs.remove('excludeAuto') ?: false;
        def languages = translateService.getLanguages()
        select << "<select name=\"${attrs.remove('name')?.encodeAsHTML()}\" "
        select << ">"
        def selected = ""
        for(langName in languages.keySet()) {
            if(langName != "AUTO_DETECT" || !excludeAuto) {
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
        def text = attrs?.text
        def detect = translateService.detect(text)
        def languages = translateService.getLanguages()
        for(lang in languages) {
            if(lang.value.equals(detect)) {
                detect = lang.key
                break;
            }
        }
        out << detect
    }
    
    def getLanguageName = { attrs ->
        def code = attrs?.code
        def languages = translateService.getLanguages()
        for(lang in languages) {
            if(lang.value.equals(code)) {
                code = lang.key
                break;
            }
        }
        out << code
    }
}
