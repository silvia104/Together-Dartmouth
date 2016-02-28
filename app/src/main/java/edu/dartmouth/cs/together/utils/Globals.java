package edu.dartmouth.cs.together.utils;


import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.together.data.Event;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class Globals {
    public static String[] sports = {"Soccer","Skiing","Cycling","Jogging","Hiking",
            "Tennis","Skating","Dancing","Gym","Basketball","Bowling","Billiard"};
    public static String[] life = {"Movie","Party","Shopping","Dining", "Travel", "Study"};
    public static List<String> categories = new ArrayList<>();
    static{
        for (String s : sports){
            categories.add(s);
        }
        for (String s: life){
            categories.add(s);
        }
    }
    public static Event event = new Event();
}
