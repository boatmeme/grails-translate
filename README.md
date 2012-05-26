# Translate - Language Translation Plugin for Grails

## Description

The Translate plugin provides a Grails Service, TagLib, and Controller to enable translation of text within your Grails application.

This plugin is powered by the [Microsoft Translator API](http://www.microsofttranslator.com/dev/) with the help of [microsoft-translator-java-api](https://github.com/boatmeme/microsoft-translation-java-api), a compact, fast Java library that wraps the Microsoft Translator AJAX Services, written specifically to support this plugin.

The TranslateService is backed by a configurable Least-Recently-Used (LRU) Cache to reduce the API calls made, particularly for oft-repeated translations (if you're dynamically translating i18n messages to foreign languages, for instance).

## Requirements

* Requires a Bing AppID, freely obtainable from the [Bing Developer Center](http://www.bing.com/developers/createapp.aspx).

## Installation

Enter your application directory and run the following from the command line: 

    grails install-plugin translate

After you have installed the Translate plugin in your application, I'd recommend you point your browser to the Plugin test page to verify all is working and familiarize yourself with the functionality it provides:

    http://localhost:8080/myAppContext/translate

## Configuration

The TranslatePlugin may be configured with several parameters, all specified in your application's */grails-app/conf/Config.groovy*


    translate.microsoft.clientId		= 'MY_CLIENT_ID'	// Windows Azure Marketplace Client ID (REQUIRED)
    translate.microsoft.clientSecret		= 'MY_CLIENT_SECRET'	// Windows Azure Marketplace Client Secret (REQUIRED)
    translate.translation.cache.maxSize		= 1000			// Maximum size of the LRU Cache for Translations
    translate.detection.cache.maxSize		= 1000 			// Maximum size of the LRU Cache for Language Detection

***
### translate.microsoft.clientId

_**REQUIRED**_

This is your Windows Azure Marketplace Client ID that you received by registering your application as described [here](http://msdn.microsoft.com/en-us/library/hh454950.aspx). It is required to use this plugin.

***
### translate.microsoft.clientSecret
_**REQUIRED**_

This is your Windows Azure Marketplace Client Secret that you received by registering your application as described [here](http://msdn.microsoft.com/en-us/library/hh454960.aspx). It is required to use this plugin.

***
### translate.translation.cache.maxSize

This is the maximum number of Translation API calls that will be stored in the LRU Cache at any given time. 
When this number is exceeded, the Least-Recently-Used entry in the Cache will be evicted.

If you do not wish to cache the Translation API calls, set this value to **-1**

_Defaults to 1000 entries_

***
### translate.detection.cache.maxSize

This is the maximum number of Language Detection API calls that will be stored in the LRU Cache at any given time. 
When this number is exceeded, the Least-Recently-Used entry in the Cache will be evicted.

If you do not wish to cache the Language Detection API calls, set this value to **-1**

_Defaults to 1000 entries_

# TranslateService

## Services

***
### translate(originText,toLang)    

Takes a String to be translated, the from language, and the to language and calls the Microsoft Translator API
Returns the results.

There is no FROM language, it relies on Microsoft to detect the origin language of the text
     
The TO Language can either be a String representing the language abbreviation (ex. "en" or "fr") OR
it can be an instance of the Language Enum (`com.memetix.mst.language.Language`)
     
Throws InvalidLanguageExceptions if the from or to language is invalid.

Sets the HTTP_REFERRER with the `grails?.serverURL` property from Config.groovy

If the user has set an API Key, method will send that, also

_Parameters_

* **originText** - the String to be translated
* **toLang** - a two-character String or com.memetix.mst.language.Language enum instance - the Language to translate TO

_Returns_

* A String, the translated text

Example:

    def originalText = "This is a string of text"
    def toLang = "fr"
    
    translateService?.translate(originalText, toLang)

_returns_

    Il s'agit d'une chaine de texte
    
***
### translate(originText,fromLang,toLang)    

Takes a String to be translated, the from language, and the to language and calls the Microsoft Translator API
Returns the results.
     
The FROM and TO Language can either be a String representing the language abbreviation (ex. "en" or "fr") OR
it can be an instance of the Language Enum (`ccom.memetix.mst.language.Language`)
     
Throws InvalidLanguageExceptions if the from or to language is invalid.

Sets the HTTP_REFERRER with the `grails?.serverURL` property from Config.groovy

If the user has set an API Key, method will send that, also

_Parameters_

* **originText** - the String to be translated
* **fromLang** - a two-character String or com.memetix.mst.language.Language - the _source_ Language to translate FROM
* **toLang** - a two-character String or com.memetix.mst.language.Language - the _target_ Language to translate TO

_Returns_

* A String, the translated text

Example:

    def originalText = "This is a string of text"
    def fromLang = "en"
    def toLang = com.memetix.mst.language.Language.FRENCH
    
    translateService?.translate(originalText, fromLang, toLang)

_returns_

    Il s'agit d'une chaine de texte

***
### detect(originText)                         
    
Takes a text string and attempts to determine the origin language. Returns the language code detected by the Microsoft Translator API

If the user has set an API Key, method will send that, also
     
_Parameters_

* **originText** - A String used to detect the language

_Returns_
     
* A String representing a 2-character language code; Microsoft's best guess at a Language.

Example:

    def originalText = "This is a string of text"
    translateService?.detect(originalText)

_returns_

    en
    
_**Hint**: if you would like the full language name, call the getLanguageName() service passing the language code as the parameter.
This example would return `ENGLISH`._

***
### getLanguageName(code)                         
    
Returns the full name of the language corresponding to the language code passed in
 
The code is a two-letter ISO Language Code supported by the Microsoft Translator API
     
_Parameters_

* **code** -- A two-character string

_Returns_
     
* A String representing full name of the language associated with the code. `null` if no match

Example:

    def langCode = "en"
    translateService?.getLanguageName(langCode)

_returns_

    ENGLISH
    
***
### getLanguages()                         
    
Returns a Map of all of the languages supported by the Microsoft Translator API
 
Key = The full name of the Language
Value = The two-character ISO Abbreviation that is required by the Microsoft Translator  API

The Value is the one that should be used on all TranslateService() method calls

_Returns_
     
*  A Map with key/value of Language Name / Language Abbreviation. In alphabetical order, by key

Example:

    translateService?.getLanguages()

_returns_

    [AFRIKAANS:af, 
     ALBANIAN:sq, 
     AMHARIC:am, 
     ARABIC:ar, 
     ARMENIAN:hy, 
     AUTO_DETECT:, 
     AZERBAIJANI:az
     ...
     ...
     UKRANIAN:uk, 
     URDU:ur, 
     UZBEK:uz, 
     VIETNAMESE:vi, 
     WELSH:cy, 
     YIDDISH:yi]

# TranslateTagLib

## Tags

All Translate tags exist in the `translate` namespace.

***
### languageSelect

Builds a &lt;SELECT&gt; list with all of the possible language choices.

_Parameters_

* **name** - the name of the lt;SELECT&gt; form element
* **value** - the two-letter code to mark as _SELECTED_
* **excludeAuto** - Exclude the AUTO_DETECT language. Useful if you want to force the user to pick a language. _Defaults to `false`_

_Example_

    <translate:languageSelect value="en" name="targetLanguage" />

_results in_

     <select name='targetLanguage'>
         <option value="af">AFRIKAANS</option>
         <option value="af">ALBANIAN</option>
         <option value="af">AMHARIC</option>
         ...
         <option value="">AUTO_DETECT</option>
         ...
         <option SELECTED value="en">ENGLISH</option>
         ...
         ...
         <option value="vi">VIETNAMESE</option>
         <option value="cy">WELSH</option>
         <option value="yi">YIDDISH</option>
     </select>
     
***
### translateText

Translates the text inside the tags to the language specified by the toLang parameter

_Parameters_

* **toLang** - The two-letter character code representing the target language for the translation
* **fromLang** - The optional two-letter character code representing the source language of the text. _Defaults to `AUTO\_DETECT`_

_Example_

    <translate:translateText toLang='fr'>This is an english phrase I would like translated</translate:translateText>

_results in_

     Il s'agit d'une phrase en anglais que je voudrais traduire
     
***
### detectLanguage

Prints the full name of the detected language of the text in the `text` parameter

_Parameters_

* **text** - The text for which we would like to know the native language

_Example_

    <translate:detectLanguage text="Il s'agit d'une phrase en anglais que je voudrais traduire"/>

_results in_

     FRENCH
     
***
### getLanguageName

Prints the full name of the language for the specified code

_Parameters_

* **code** - The two letter ISO Language code for which we would like to know the full name

_Example_

    <translate:getLanguageName code=\"fr\"/>
    
_results in_

     FRENCH

# TranslateController

## Actions

***
### /translate/

Provides a test form for validating the functionality of the Translate plugin. May serve as a template for your own application.

***
### /translate/ajax

 Translate AJAX Action, accepts params and returns an HTML fragment, JSON, or XML

_Parameters_

* **originalText** - the text to be translated
* **toLang** - the two letter ISO language code of the target translation language
* **fromLang** - the two letter ISO language code of the source translation language - if not provided, defaults to AUTO_DETECT
* **format** - `json`, `xml`, or `html`. Determines the format of the response. Defaults to `json`.

> _The originalText and toLang paramter must be supplied, or the response will return a 500 status\_code_

For example, doing an HTTP GET on this URL:

    app-context/translate/ajax?originalText=This%20is%20an%20english%20phrase%20I%20would%20like%20translated&toLang=fr

might return the following JSON:


    {
      "status_code":"200",
      "status_text":"OK",
      "elapsedTime":13,
      "errors":[],
      "data": ["translation":"Il s'agit d'une phrase en anglais que je voudrais traduire"]
    }


OR the following XML:

 
    <?xml version='1.0' encoding='UTF-8'?>
     <response>
         <status_code>200</status_code>
         <status_text>OK</status_text>
         <errors></errors>
         <data>
         	<translation>Il s'agit d'une phrase en anglais que je voudrais traduire</translation>
         </data>
         <elapsedTime>147</elapsedTime>
     </response>
     
***
### /translate/detectAjax

 Detect Language AJAX Action, accepts params and returns an HTML fragment, JSON, or XML

_Parameters_

* **originalText** - the text for which we would like to detect the language
* **format** - `json`, `xml`, or `html`. Determines the format of the response. Defaults to `json`.

> _The originalText paramter must be supplied, or the response will return a 500 status\_code_

For example, doing an HTTP GET on this URL:

    app-context/translate/detectAjax?originalText=This%20is%20an%20english%20phrase%20I%20would%20like%20detected

might return the following JSON:


    {
      "status_code":"200",
      "status_text":"OK",
      "elapsedTime":13,
      "errors":[],
      "data": ["code":"en","language":"ENGLISH"]
    }


OR the following XML:

 
    <?xml version='1.0' encoding='UTF-8'?>
     <response>
         <status_code>200</status_code>
         <status_text>OK</status_text>
         <errors></errors>
         <data>
         	<language>
         		<code>en</code>
         		<name>ENGLISH</name>
          	</language>
         </data>
         <elapsedTime>147</elapsedTime>
     </response>

## Other plugins

Bradley Beddoes' [Auto Translate](http://bradleybeddoes.com/2010/11/grails-auto-translate-plugin/)

This plugin auto-generates Grails' i18n message files for other languages using the Google Translation API. Auto-translate is a script that you run in more of a one-off context than a full Grails integration with Google Translation. 

## Source Code @ GitHub

The source code is available on GitHub at [https://github.com/boatmeme/grails-translate](https://github.com/boatmeme/grails-translate). 

Find a bug? Fork it. Fix it. Issue a pull request.

    git clone git://github.com/boatmeme/grails-translate

Contributions welcome!

## Issue Tracking @ GitHub

Issue tracking is also on GitHub at [https://github.com/boatmeme/grails-translate/issues](https://github.com/boatmeme/grails-translate/issues).

Bug reports, Feature requests, and general inquiries welcome.

## Contact

Feel free to contact me by email (jonathan.griggs at gmail.com) or follow me on GitHub at [https://github.com/boatmeme](https://github.com/boatmeme).

# Change Log

## v1.3.0 - 2012.05.25

* Upgraded to 0.6.1 RELEASE of Microsoft Translator Java API from Maven Central Repo. Allows the use of new Windows Azure Marketplace Client ID / Client Secret Oauth

## v1.2.1 - 2012.01.05

* Now using 0.5 RELEASE version of Microsoft Translator Java API from Maven Central Repo

## v1.2 - 2011.12.17

* Upgraded to Grails 2.0
* Fixed bug where Language.setKey() was not being called prior to first call

## v1.1.2 - 2011.12.17

* Updated to use 0.5-SNAPSHOT version of Microsoft Translator Java API

## v1.1.1 - 2011.06.15

* Upgraded to use 0.4-SNAPSHOT version of Microsoft Translator Java API
* Implemented BreakSentences service

## v1.1 - 2011.06.01

* Migrated off of Google Translation API and onto the Microsoft Translator API
* translate.microsoft.apiKey is now required to be specified in Config.groovy
* Implemented Language Name Localization - Get the language names constants _in_ the language of your choosing
* Small bug fixes and error checking

## v1.0 - 2011.05.27

* Initial release
