package com.memetix.translate

/**
 * TranslationException
 * @author Jonathan Griggs
 * 
 */
class TranslationException extends RuntimeException {
        String message 
        String originalText
        String fromLanguage
        String toLanguage
}

