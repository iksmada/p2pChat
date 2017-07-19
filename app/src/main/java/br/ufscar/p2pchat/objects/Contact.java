package br.ufscar.p2pchat.objects;

/**
 * Created by adamski on 18/07/2017.
 */

public class Contact{
    private String name;
    private String IP;

    public Contact(String name, String IP){
        this.name = name;
        this.IP = IP;
    }

    public String getName() {
        return name;
    }

    public String getIP() {
        return IP;
    }

    @Override
    public String toString() {
        return name + " " + IP;
    }
}
