package hk.ust.cse.hunkim.questionroom.hashtag_extracter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by PakShing on 16/11/2015.
 */
public class Hashtag_extracter {

    private List<String> list = new ArrayList<String>();
    public Hashtag_extracter() {}
    public Hashtag_extracter(String s)
    {
        this.list = ExtractHashTag(s);
    }

    public List<String> ExtractHashTag(String s)
    {
        List<String> list = new ArrayList<String>();
        String[] Hashtag_str = s.split("[^#a-zA-Z0-9']+");

        for (int i = 0; i < Hashtag_str.length; i++)
        {
            String temp = CheckHashtag(Hashtag_str[i]);	//Returned string are in lower case
            if (temp != null && !list.contains(temp))
                list.add(temp);

        }

        return list;
    }

    public String CheckHashtag(String s)
    {
        s = s.replaceAll("[^#A-Za-z0-9]", "");	//Clear all the non-alphanumeric characters
        int firstHashtagIndex = s.indexOf("#");	//Find the first hashtag location
        if (firstHashtagIndex > -1)	//If it is found (i.e. != -1), then perform the following, otherwise null
        {
            if (s.indexOf("#", firstHashtagIndex + 1) == -1 && s.length() != 1)	//Ensure there is only 1 hashtag
                return s.toLowerCase();	//Must make it all lower case, otherwise #A and #a will be two different things
        }

        return null;
    }

    public List<String> getList(){ return this.list; }
    public String[] getArray() { return this.list.toArray(new String[list.size()]); }
    public boolean IsEmptyList() { return this.list.isEmpty();}
    public int getListCount() { return this.list.size();}
    public String getListItem(int i) { return this.list.get(i); }
    //This function will be remade as pushing all firebase values
    //The possible types returned by getValue() are Boolean, String, Long, Double, Map<String, Object>, and List<Object>.
    //If no data exists at the location, the snapshot will return null.
    public void ListAllItems(List<String> list)
    {
        if (list.isEmpty())
            System.out.println("Empty List.");
        else
        {
            for (int i = 0; i < list.size(); i++)
                System.out.println(list.get(i));
        }
    }
}
