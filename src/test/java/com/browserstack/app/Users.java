package com.browserstack.app;

class SearchElement {
    public String search;
    public Boolean busy;
    
// class constructor
SearchElement(String search, Boolean busy)
{
    this.search = search;
    this.busy = busy;
}
}

public class Users {
    static SearchElement[] arr = {new SearchElement("google", false),new SearchElement("Browserstack", false),new SearchElement("automation testing", false)};
    
}
