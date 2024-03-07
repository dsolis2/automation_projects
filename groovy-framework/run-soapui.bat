@echo off
@setLocal

set SOAP_HOME_PATH=C:\Program Files (x86)\SmartBear\SoapUI-5.0.0

set SOAP_PROJECT_XML="PRE-Regression.xml"

set TEST_SUITE=Automation_Test_Suite

set TEST_CASE="Run Script"

set INPUT_FILE="System_Test_Cases_Groovy_Input.xls"

set PARALLEL_RUN=false

set IGNORE_JSON_PATH="transactionIdentifier.transactionId,transactionId"

echo [INFO] Running SOAP_PROJECT_XML for the given TEST_SUITE and TEST_CASE

echo [INFO] Commmand run is "%SOAP_HOME_PATH%\bin\testrunner.bat" -s%TEST_SUITE% -c%TEST_CASE% -r -I %SOAP_PROJECT_XML% -PinputFileName=%INPUT_FILE% -PparallelRun=%PARALLEL_RUN% -PignoreJsonPathValidation=%IGNORE_JSON_PATH%

"%SOAP_HOME_PATH%\bin\testrunner.bat" -s%TEST_SUITE% -c%TEST_CASE% -r -I %SOAP_PROJECT_XML% -PinputFileName=%INPUT_FILE% -PparallelRun=%PARALLEL_RUN% -PignoreJsonPathValidation=%IGNORE_JSON_PATH%

echo.
echo [INFO] Please see soapui logs in %SOAP_HOME_PATH%

@endLocal
