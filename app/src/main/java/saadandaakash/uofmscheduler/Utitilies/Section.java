package saadandaakash.uofmscheduler.Utitilies;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

public class Section implements Comparator<Section>{

    // Meeting class holds basic meeting info
    public static class Meeting {
        public String days, times;
        public ArrayList<String> instructors = new ArrayList<>();
        public String location = "";

        public Meeting(String days, String times) {
            this.days = days;
            this.times = times;
        }

        public Meeting(JSONObject meeting) {
            try {
                this.times = meeting.getString("Times");
                this.days = meeting.getString("Days");
                this.location = meeting.getString("Location");
                JSONArray instructors = meeting.getJSONArray("Instructor");
                for (int i = 0; i < instructors.length(); i++) {
                    // use get instead of get____ because at this point I'm too tired to care
                    // what the object type is
                    this.instructors.add(instructors.get(i).toString());
                }
            }
            catch (JSONException j) {
                j.printStackTrace();
            }
        }

        // REQUIRES: all fields are initialized and non-null
        // MODIFIES: nothing
        // EFFECT: returns a JSON object of this Meeting
        public JSONObject getJSONFromMeeting() {
            try {
                JSONObject object = new JSONObject();
                object.put("Days", days);
                object.put("Times", times);
                object.put("Location", location);
                object.put("Instructor", new JSONArray(instructors));
                return object;
            }
            catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    // These parameters are there from the start
    public String subjectCode, catalogNumber, sectionNumber, sectionType;
    public ArrayList<Meeting> meetings;

    public String creditHours = "", availableSeats = "", enrollmentCapacity = "";

    // These parameters are added in section info fragment
    public String courseDescr = "", classTopic = "", courseTitle = "";

    public Section() {}

    public Section(String subjectCode, String catalogNumber, String sectionNumber,
                   String sectionType, ArrayList<Meeting> meetings) {
        this.subjectCode = subjectCode;
        this.catalogNumber = catalogNumber;
        this.sectionNumber = sectionNumber;
        this.sectionType = sectionType;
        this.meetings = meetings;
    }

    public String getMeetings() {
        ArrayList<String> meetings_string = new ArrayList<String>();
        for (Meeting m : meetings) {
            meetings_string.add(m.days + " " + m.times);
        }
        return TextUtils.join(", ", meetings_string);
    }

    public String getInstructors() {
        ArrayList<String> instructors_string = new ArrayList<String>();
        for (Meeting m : meetings) {
            for (String instructor : m.instructors) {
                instructors_string.add(instructor);
            }
        }
        return TextUtils.join(", ", instructors_string);
    }

    public int compare(Section s1, Section s2) {

        // first check the subject names
        if (s1.subjectCode.compareTo(s2.subjectCode) != 0) {
            return s1.subjectCode.compareTo(s2.subjectCode);
        }
        // if equal, compare course number
        if (Integer.parseInt(s1.catalogNumber) - Integer.parseInt(s2.catalogNumber) != 0) {
            return Integer.parseInt(s1.catalogNumber) - Integer.parseInt(s2.catalogNumber);
        }
        // if equal, compare section number
        return Integer.parseInt(s1.sectionNumber) - Integer.parseInt(s2.sectionNumber);
    }
}