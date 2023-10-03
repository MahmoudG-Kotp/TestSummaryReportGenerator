package data_types;

/**
 * Represents a test case with a title, start time, and result.
 */
public class TestCase {
   private String title;       // The title of the test case
   private String startTime;   // The start time of the test case
   private String result;      // The result of the test case

   /**
    * Constructor for TestCase.
    *
    * @param title     The title of the test case.
    * @param startTime The start time of the test case.
    * @param result    The result of the test case.
    */
   public TestCase(String title, String startTime, String result) {
      this.title = title;
      this.startTime = startTime;
      this.result = result;
   }

   /**
    * Sets a new title for the test case.
    *
    * @param title The new title for the test case.
    */
   public void editTitle(String title) {
      this.title = title;
   }

   /**
    * Sets a new start time for the test case.
    *
    * @param startTime The new start time for the test case.
    */
   public void editTime(String startTime) {
      this.startTime = startTime;
   }

   /**
    * Sets a new result value for the test case.
    *
    * @param result The new result value for the test case.
    */
   public void editResult(String result) {
      this.result = result;
   }

   /**
    * Gets the title of the test case.
    *
    * @return The title of the test case.
    */
   public String getTitle() {
      return title;
   }

   /**
    * Gets the start time of the test case.
    *
    * @return The start time of the test case.
    */
   public String getStartTime() {
      return startTime;
   }

   /**
    * Gets the result of the test case.
    *
    * @return The result of the test case.
    */
   public String getResult() {
      return result;
   }
}

