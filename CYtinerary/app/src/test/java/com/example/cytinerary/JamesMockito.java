package com.example.cytinerary;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class JamesMockito {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    // Test 1 - Construction of a Discussion object from a JSON Object
    @Test
    public void test1() throws JSONException {

        // The JSONObject that will used in the constructor
        String post_string = "{\"coursecode\":\"CS309\",\"discussiontitle\":\"title\",\"discussiondate\":\"date\",\"discussioncontent\":\"content\",\"writerid\":\"000000000\"}";
        JSONObject post= new JSONObject(post_string);

        // The constructor we are trying to test
        Discussions.Discussion d = new Discussions.Discussion(post);

        // The mocked version of a Discussion object
        Discussions.Discussion md = mock(Discussions.Discussion.class);

        // Mocking the behavior of getting the values from the Discussion object
        when(md.getCoursecode()).thenReturn("CS309");
        when(md.getDiscussiontitle()).thenReturn("title");
        when(md.getDiscussiondate()).thenReturn("date");
        when(md.getDiscussioncontent()).thenReturn("content");

        // asserting that the mock Discussion has its fields equivalent to the Discussion constructed from JSONObject
        assertEquals(md.getCoursecode(), d.getCoursecode());
        assertEquals(md.getDiscussiontitle(), d.getDiscussiontitle());
        assertEquals(md.getDiscussiondate(), d.getDiscussiondate());
        assertEquals(md.getDiscussioncontent(), d.getDiscussioncontent());
    }

    // Test 2 - Testing parcelling of an Assignment object, i.e., is it parcellable and can it be written
    // to a parcel from a assignment object and then back into a new Assignment object and both be equivalent
    @Test
    public void test2() throws JSONException {

        // The parcel that will be used
        Parcel p = Parcel.obtain();

        // Constructing a JSONObject
        String post_string = "{\"coursecode\":\"CS309\",\"assignmenttitle\":\"title\",\"duedate\":\"date\",\"description\":\"description\"}";
        JSONObject post= new JSONObject(post_string);

        // Constructing an Assignment from JSON Object initially
        Assignments.Assignment ja = new Assignments.Assignment(post);

        // Writing d into Parcel p
        ja.writeToParcel(p,0);
        p.setDataPosition(0);

        // Constructing an Assignment from the parcel p
        Assignments.Assignment a = new Assignments.Assignment(p);

        // The mock Assignment
        Assignments.Assignment ma = mock(Assignments.Assignment.class);

        // Mocking the behavior of getting the values from the Assignment object
        when(ma.getCoursecode()).thenReturn("CS309");
        when(ma.getAssignmenttitle()).thenReturn("title");
        when(ma.getDuedate()).thenReturn("date");
        when(ma.getDescription()).thenReturn("description");

        // First lets compare a and ja (parcel created and json created assignments)
        assertEquals(ja.getCoursecode(), a.getCoursecode());
        assertEquals(ja.getAssignmenttitle(), a.getAssignmenttitle());
        assertEquals(ja.getDuedate(), a.getDuedate());
        assertEquals(ja.getDescription(), a.getDescription());

        // now asserting that the mock Discussion has its fields equivalent to the Discussion constructed from JSONObject
        assertEquals(ma.getCoursecode(), a.getCoursecode());
        assertEquals(ma.getAssignmenttitle(), a.getAssignmenttitle());
        assertEquals(ma.getDuedate(), a.getDuedate());
        assertEquals(ma.getDescription(), a.getDescription());
    }
}
