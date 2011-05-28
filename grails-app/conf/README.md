# Config.groovy

This file contains properties required for Unit Testing, chiefly, your Microsoft Translator API Key. 

    translate.test.microsoft.apiKey = ""

This API Key is _only_ used for Unit Testing. For normal usage, the API Key is set by the plugin's client application

If you've forked this branch and wish to run the Unit Tests, please replace the placeholder value with _your_ API Key. You should then be careful not to check this file back into your branch. Ignore the changes locally by issuing the following command

    git update-index --assume-unchanged <relative_path_to>/Config.groovy