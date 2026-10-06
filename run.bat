@echo off
javac LostLinkApp.java
if errorlevel 1 pause & exit /b 1
java LostLinkApp
