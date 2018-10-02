# RestSMS  
☝ The app is currently work in process.  There is no stable version at the moment and there can be breaking changes, if you use the master-branch.

--- 

The RestSMS-App allows you to send SMS via Webservice from your Android-Device.  

### Requirements:
- Android Version 6.0 or higher
- Android-Device which is able to send SMS

### Android-Limit:
Android´s default SMS-Limit are 30 SMS to a single phonenumber within 30 minutes.  
You can change your SMS-Limit for your device (root-permission is **not** required).
#### How to change Android-Limit:
1. Make sure you have enabled USB-Debugging on your device and you are ready to use ADB.
2. Connect your device to the pc and open the terminal.
3. Open the adb-shell via the command: `adb shell`
4. Change the value of the SMS-Limit to the number of SMS you want to send within the 30 minutes timeframe. Via the command:  
`settings put global sms_outgoing_check_max_count 100`   
This command allows you to send 100 SMS to a phonenumber within the 30 minutes timeframe.
5. If you want to also change the timeframe, you can use the command:  
`settings put global sms_outgoing_check_interval_ms 900000`  
This command reduces the timeframe to 15 minutes.  
If you entered both commands, you would be able to send 100 SMS to a phonenumber within 15 minutes.
### API-Usage
In Progress ...