package net.xcreen.restsms;

public class SMSResponse {

    public boolean success;
    public String message;

    public SMSResponse(boolean success, String message){
        this.success = success;
        this.message = message;
    }

    public SMSResponse(){
        this.success = false;
    }
}
