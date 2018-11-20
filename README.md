# RestSMS  

The RestSMS-App allows you to send SMS via Webservice from your Android-Device.  

![Architecture](diagram_draw.io.png "RestSMS API Architecture")

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
The default server-url is `http://127.0.0.1:8080/`.  
You can change the port in the settings-menu. The ip can be changed via the public network address, so you can access the api from anywhere.  
To send a SMS you can point to your address and use the /send-Endpoint (eg. `http://127.0.0.1:8080/send`).  
You have to send `phoneno` and `message` as post-parameter (you can use `form-data` and also `x-www-form-urlencoded`).  
A response you get a JSON with a `success` and a `message` variable.

#### Example Curl (x-www-form-urlencoded)
message = "Hello World"  
phoneno = "+4915100000000"
```shell
curl -X POST http://127.0.0.1:8080/send -H 'Cache-Control: no-cache' -H 'Content-Type: application/x-www-form-urlencoded' -d 'message=Hello%20World&phoneno=%2B4915100000000'
```

#### Successful Response
```json
{
    "success": true
}
```
⚠️ That means the SMS got forwarded to your default SMS-App (packagename can be found under App-Information). The SMS still can get stuck in your default SMS-App. For example on a Emulator, you will get success = true, but the default SMS-App cant send the SMS, because the SIM-Card is just emulated. So success = true, does not mean that the SMS was already successful sent.

#### Failed Response
```json
{
    "message": "message or phoneno parameter are missing!",
    "success": false
}
```
The message variable holds the error-message, so you can adjust your request.