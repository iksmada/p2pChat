package br.ufscar.p2pchat.objects;

/**
 * Created by adamski on 18/07/2017.
 */

public class Message{
    private String content;
    private String IP;

    public Message(String content, String IP){
        this.content = content;
        this.IP = IP;
    }

    public String getContent() {
        return content;
    }

    public String getIP() {
        return IP;
    }

    @Override
    public String toString() {
        return content + " " + IP;
    }
}
