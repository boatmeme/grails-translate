# Translate - Google Translate API Plugin for Grails

## Description

The Translate plugin provides a Grails Service, TagLib, and Controller to enable translation of text within your Grails application.

This plugin wraps the Google Translate API with the help of the unofficial [Java Client](https://github.com/richmidwinter/google-api-translate-java).

The TranslateService is backed by a configurable Least-Recently-Used (LRU) Cache to reduce the API calls made, particularly for oft-repeated
translations (dynamically translated I18N messages, for instance).

## Installation

Enter your application directory and run the following from the command line: 

    grails install-plugin translate

After you have installed the Translate plugin in your application, I'd recommend you point your browser to the Plugin test page to verify all is working and familiarize yourself with the functionality it provides:

    http://localhost:8080/myAppContext/translate

## Configuration

The TranslatePlugin may be configured with several parameters, all specified in your application's */grails-app/conf/Config.groovy*


    translate.google.apiKey			= 'MY_API_KEY'	// Google Translate API Key (OPTIONAL)
    translate.translation.cache.maxSize		= 1000		// Maximum size of the LRU Cache for Translations
    translate.detection.cache.maxSize		= 1000 		// Maximum size of the LRU Cache for Language Detection

***
### translate.google.apiKey

_OPTIONAL_

This is your Google API Key that you received by signing up at the [Google API Console](https://code.google.com/apis/console). It is
not required, but if you set this property, you will be able to track your API Usage Metrics at the Google API Console.

It is also just good manners to use an API Key so that Google may be able to contact you if necessary.

With or without the API Key, there is a 100,000 character per day courtesy limit. 

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

Takes a String to be translated, the from language, and the to language and calls the Google Translation API
Returns the results.

There is no FROM language, it relies on Google to detect the origin language of the text
     
The TO Language can either be a String representing the language abbreviation (ex. "en" or "fr") OR
it can be an instance of the Google API package Language Enum (`com.google.api.translate.Language`)
     
Throws InvalidLanguageExceptions if the from or to language is invalid.

Sets the Google-required HTTP_REFERRER with the `grails?.serverURL` property from Config.groovy

If the user has set an API Key, method will send that, also

_Parameters_

* a String - the String to be translated
* a String or com.google.api.translate.Language - the Language to translate TO

_Returns_

* A String, the translated text

Example:

    def originalText = "This is a string of text"
    def toLang = "fr"
    
    translateService?.translate(originalText, fromLang, toLang)

_returns_

    Il s'agit d'une cha�ne de texte
    
***
### translate(originText,fromLang,toLang)    

Takes a String to be translated, the from language, and the to language and calls the Google Translation API
Returns the results.
     
The FROM and TO Language can either be a String representing the language abbreviation (ex. "en" or "fr") OR
it can be an instance of the Google API package Language Enum (`com.google.api.translate.Language`)
     
Throws InvalidLanguageExceptions if the from or to language is invalid.

Sets the Google-required HTTP_REFERRER with the `grails?.serverURL` property from Config.groovy

If the user has set an API Key, method will send that, also

_Parameters_

* a String - the String to be translated
* a String or com.google.api.translate.Language - the Language to translate FROM
* a String or com.google.api.translate.Language - the Language to translate TO

_Returns_

* A String, the translated text

Example:

    def originalText = "This is a string of text"
    def fromLang = "en"
    def toLang = com.google.api.translate.Language.FRENCH
    
    translateService?.translate(originalText, fromLang, toLang)

_returns_

    Il s'agit d'une cha�ne de texte

***
### detect(originText)                         
    
Takes a text string and attempts to determine the origin language. Returns the language code detected by the Google Language API

If the user has set an API Key, method will send that, also
     
_Parameters_

* originText A String used to detect the language

_Returns_
     
* A String representing a 2-character language code; Google's best guess at a Language.
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
 
The code is a two-letter ISO Language Code supported by the Google Translation API
     
_Parameters_

* code -- A two-character string

_Returns_
     
* A String representing full name of the language associated with the code. `null` if no match

Example:

    def langCode = "en"
    translateService?.getLanguageName(langCode)

_returns_

    ENGLISH
    
***
### getLanguages()                         
    
Returns a Map of all of the languages supported by the Google Translation API
 
Key = The full name of the Language
Value = The two-character ISO Abbreviation that is required by the Google Translate API

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



    <translate:languageSelect value="en" />


_results in_

    I just tweeted this URL so you could see it http://www.cbsnews.com/8301-503543_162-20063168-503543.html 
    and also this one http://iamthetrend.com/2011/02/10/10-examples-of-awesome-indie-clothing-look-books/
***
### expandAndLinkUrls


    <unshorten:expandAndLinkUrls linkClass="myLinkClass">
        I just tweeted this URL so you could see it http://bit.ly/jkD0Qr, 
        and also this one http://t.co/8lrqrZf
    </unshorten:expandAndLinkUrls>

_results in_

    I just tweeted this URL so you could see it 
    <a class="myLinkClass" href="http://www.cbsnews.com/8301-503543_162-20063168-503543.html">
        http://www.cbsnews.com/8301-503543_162-20063168-503543.html 
    </a>
    and also this one 
    <a class="myLinkClass" href="http://iamthetrend.com/2011/02/10/10-examples-of-awesome-indie-clothing-look-books/">
       http://iamthetrend.com/2011/02/10/10-examples-of-awesome-indie-clothing-look-books/
    </a>
***
### unshortenUrl


    <unshorten:unshortenUrl url='http://bit.ly/jkD0Qr'/>

_results in_


    http://www.cbsnews.com/8301-503543_162-20063168-503543.html

***
### unshortenAndLinkUrl


    <unshorten:unshortenAndLinkUrl class="myLinkClass" url='http://bit.ly/jkD0Qr'/>


_results in_


    <a class="myLinkClass" href="http://www.cbsnews.com/8301-503543_162-20063168-503543.html">
        http://www.cbsnews.com/8301-503543_162-20063168-503543.html 
    </a>


# <p id="controller">UnshortenController</p>

## Actions

***
### /unshorten/

Provides a test form for validating the functionality of the Unshorten plugin and testing individual URLs. May serve as a template for your own application.
***
### /unshorten/ajax

 AJAX Action, accepts params and returns an HTML fragment, JSON, or XML

_Parameters_

* **shortUrl** - one or more URLs
* **shortText** - one or more blocks of text that may contain shortened links (i.e. Tweets)
* **format** - `json`, `xml`, or `html`. Determines the format of the response. Defaults to `json`.

> _At least 1 shortUrl OR shortText must be supplied, or the response will return a 500 status\_code_

For example, doing an HTTP GET on this URL:

    app-context/unshorten/ajax?shortUrl=http://bit.ly/jkD0Qr&shortUrl=http://t.co/8lrqrZf&shortText=Tweet!%20http://bit.ly/11Da1f

might return the following JSON:


    {
      "status_code":"200",
      "status_text":"OK",
      "elapsedTime":13,
      "errors":[],
       "data":
           [
               {
                "cached":false,
                "fullUrl":"http://iamthetrend.com/2011/02/10/10-examples-of-awesome-indie-clothing-look books/",
                "status":"UNSHORTENED",
                "shortUrl":"http://t.co/8lrqrZf"
                "type":"url"
               },
               {
                 "cached":false,
                 "fullUrl":"http://www.cbsnews.com/8301-503543_162-20063168-503543.html",
                 "status":"UNSHORTENED",
                 "shortUrl":"http://bit.ly/jkD0Qr"
                 "type":"url"
                },
                {
                 "fullText":"Tweet! http://twitcaps.com",
                 "shortText":"Tweet! http://bit.ly/11Da1f"
                 "type":"text"
                }
            ]
    }


OR the following XML:

 
    <response>
        <status_code>200</status_code>
        <status_text>OK</status_text>
        <errors />
        <data>
            <entry>
                <type>url</type>
                <shortUrl>http://t.co/8lrqrZf</shortUrl>
                <fullUrl>
                     http://iamthetrend.com/2011/02/10/10-examples-of-awesome-indie-clothing-look-books/
                </fullUrl>
                <status>UNSHORTENED</status>
                <cached>false</cached>
            </entry>
            <entry>
                <type>url</type>
                <shortUrl>http://bit.ly/jkD0Qr</shortUrl>
                <fullUrl>
                    http://www.cbsnews.com/8301-503543_162-20063168-503543.html
                 </fullUrl>
                <status>UNSHORTENED</status>
                <cached>false</cached>
            </entry>
            <entry>
                <type>text</type>
                <shortText>Tweet! http://bit.ly/11Da1f</shortText>
                <fullText>Tweet! http://twitcaps.com/</fullText>
            </entry>
        </data>
        <elapsedTime>91</elapsedTime>
    </response>


## Other plugins

[urlreversi: Revert your shortened URLs](http://grails.org/plugin/urlreversi)

The urlreversi plugin has been around for quite a while longer than Unshorten and provides the basic functionality of Unshortening (shortUrl-in / fullUrl-out) in a Service as well as a TagLib for convenience.

While it does not feature a Caching implementation as far as I can tell, it should not be too difficult to implement your own cache around its functionality.

## Source Code @ GitHub

The source code is available on GitHub at [https://github.com/boatmeme/grails-unshorten](https://github.com/boatmeme/grails-unshorten). 

Find a bug? Fork it. Fix it. Issue a pull request.

Contributions welcome!

## Issue Tracking @ GitHub

Issue tracking is also on GitHub at [https://github.com/boatmeme/grails-unshorten/issues](https://github.com/boatmeme/grails-unshorten/issues).

Bug reports, Feature requests, and general inquiries welcome.

## Contact

Feel free to contact me by email (jonathan.griggs at gmail.com) or follow me on GitHub at [https://github.com/boatmeme](https://github.com/boatmeme).

# Change Log

## v1.0.4 - 2011.05.26

* `unshorten.http.readTimeout` property was incorrectly named
* Cosmetic changes on the test view, `app_context/unshorten`

## v1.0.3 - 2011.05.22

* Fixed bug where URL Status was being set to UNKNOWN when it should be set to TIMED_OUT
* AJAX response can now return HTML
* AJAX format parameter supports 'html' value
* Support for 3 new configuration options:


           unshorten.ajax.forward.html   = [controller:'myController', action:'myAction']
           unshorten.ajax.forward.json   = [controller:'myController', action:'myAction']
           unshorten.ajax.forward.xml    = [controller:'myController', action:'myAction']

These can be (optionally) set to a map with the 'controller' and 'action' in your application to forward the results of the Unshorten AJAX action. By specifying these options you can process or style the data before returning it to the browser.

## v1.0.2 - 2011.05.20

* Added UnshortenService.expandUrlsInTextAll() to take a list of 1 - n text blocks and return the results of expanding all of them
* AJAX action now supports �shortText� parameter which operates on blocks of text instead of individual urls
* AJAX response �data� object now returns �type� property. This can be either �url� or �text�
* AJAX response now returns �elapsedTime� property (time of call in milliseconds)
* AJAX response can now return XML
* AJAX action now supports �format� parameter which can be either �json� or �xml�. Defaults to �json�
* Added UrlStatus Enum to UnshortenService

## v1.0.1 - 2011.05.19 

* Added support for redirects via HTTP 302 and 303 (bad shortener!)
* Added support for chaining redirects
* Fixed bug with relative redirects
* Added status for "redirect"

## v1.0 - 2011.05.17

* Initial release